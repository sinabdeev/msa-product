package ru.example.product.processing.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import ru.example.product.processing.domain.ProductCategory
import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.domain.TagsWrapper
import ru.example.product.processing.repository.ProductRepository
import ru.example.product.processing.service.ProductService
import ru.example.product.processing.service.status.history.ProductStatusHistoryService
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
class ProductStatusTransitionIntegrationTest {

    @Autowired
    private lateinit var productService: ProductService

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var historyService: ProductStatusHistoryService

    @Test
    fun `test successful workflow path`() {
        // Создаем продукт со статусом DRAFT
        val product = createProduct(status = ProductStatus.DRAFT)
        val productId = product.id!!

        // Выполняем переходы по пути 1
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW, "Отправлен на проверку")
        assertStatus(productId, ProductStatus.PENDING_REVIEW)

        productService.updateStatus(productId, ProductStatus.REVIEWED, "Проверен модератором")
        assertStatus(productId, ProductStatus.REVIEWED)

        productService.updateStatus(productId, ProductStatus.APPROVED, "Утвержден менеджером")
        assertStatus(productId, ProductStatus.APPROVED)

        productService.updateStatus(productId, ProductStatus.ACTIVE, "Активирован для продажи")
        assertStatus(productId, ProductStatus.ACTIVE)

        productService.updateStatus(productId, ProductStatus.PROCESSED, "Заказ обработан")
        assertStatus(productId, ProductStatus.PROCESSED)

        productService.updateStatus(productId, ProductStatus.SHIPPED, "Отправлен клиенту")
        assertStatus(productId, ProductStatus.SHIPPED)

        productService.updateStatus(productId, ProductStatus.ARCHIVED, "Доставлен и завершен")
        assertStatus(productId, ProductStatus.ARCHIVED)

        // Проверяем историю
        val history = historyService.getHistory(productId)
        assertEquals(7, history.size)
        assertEquals(ProductStatus.DRAFT, history[0].fromStatus)
        assertEquals(ProductStatus.PENDING_REVIEW, history[0].toStatus)
        assertEquals(ProductStatus.ARCHIVED, history.last().toStatus)
    }

    @Test
    fun `test rejection and resubmission path`() {
        val product = createProduct(status = ProductStatus.DRAFT)
        val productId = product.id!!

        // DRAFT -> PENDING_REVIEW
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW)
        // PENDING_REVIEW -> REVIEWED
        productService.updateStatus(productId, ProductStatus.REVIEWED)
        // REVIEWED -> REJECTED
        productService.updateStatus(productId, ProductStatus.REJECTED, "Не соответствует требованиям")
        assertStatus(productId, ProductStatus.REJECTED)

        // REJECTED -> PENDING_REVIEW (исправление)
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW, "Исправлены замечания")
        assertStatus(productId, ProductStatus.PENDING_REVIEW)

        // PENDING_REVIEW -> REVIEWED
        productService.updateStatus(productId, ProductStatus.REVIEWED)
        // REVIEWED -> APPROVED
        productService.updateStatus(productId, ProductStatus.APPROVED)
        assertStatus(productId, ProductStatus.APPROVED)

        val history = historyService.getHistory(productId)
        assertEquals(6, history.size)
        assertEquals(ProductStatus.REJECTED, history[2].toStatus)
        assertEquals(ProductStatus.PENDING_REVIEW, history[3].toStatus)
    }

    @Test
    fun `test invalid transition throws exception`() {
        val product = createProduct(status = ProductStatus.DRAFT)
        val productId = product.id!!

        // Попытка перейти из DRAFT сразу в REVIEWED (недопустимо)
        val exception = assertFailsWith<ru.example.product.processing.exception.InvalidStatusTransitionException> {
            productService.updateStatus(productId, ProductStatus.REVIEWED)
        }
        assertEquals(ProductStatus.DRAFT, exception.from)
        assertEquals(ProductStatus.REVIEWED, exception.to)
        // Статус не должен измениться
        assertStatus(productId, ProductStatus.DRAFT)
    }

    @Test
    fun `test get possible transitions`() {
        val product = createProduct(status = ProductStatus.DRAFT)
        val productId = product.id!!

        var possible = productService.getPossibleTransitions(productId)
        assertEquals(setOf(ProductStatus.PENDING_REVIEW, ProductStatus.ARCHIVED), possible)

        // Переходим в PENDING_REVIEW
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW)
        possible = productService.getPossibleTransitions(productId)
        assertEquals(
            setOf(ProductStatus.REVIEWED, ProductStatus.ARCHIVED, ProductStatus.DRAFT),
            possible
        )

        // Переходим в REVIEWED
        productService.updateStatus(productId, ProductStatus.REVIEWED)
        possible = productService.getPossibleTransitions(productId)
        assertEquals(setOf(ProductStatus.APPROVED, ProductStatus.REJECTED), possible)
    }

    @Test
    fun `test combination of paths`() {
        val product = createProduct(status = ProductStatus.DRAFT)
        val productId = product.id!!

        // DRAFT -> PENDING_REVIEW
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW)
        // PENDING_REVIEW -> REVIEWED
        productService.updateStatus(productId, ProductStatus.REVIEWED)
        // REVIEWED -> REJECTED
        productService.updateStatus(productId, ProductStatus.REJECTED)
        // REJECTED -> PENDING_REVIEW
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW)
        // PENDING_REVIEW -> ARCHIVED (досрочное архивирование)
        productService.updateStatus(productId, ProductStatus.ARCHIVED, "Отмена заявки")

        assertStatus(productId, ProductStatus.ARCHIVED)

        // ARCHIVED -> ACTIVE (восстановление)
        productService.updateStatus(productId, ProductStatus.ACTIVE, "Восстановлен")
        assertStatus(productId, ProductStatus.ACTIVE)

        // ACTIVE -> ARCHIVED (снова архивируем)
        productService.updateStatus(productId, ProductStatus.ARCHIVED, "Окончательное снятие")

        val history = historyService.getHistory(productId)
        assertEquals(7, history.size)
    }

    private fun createProduct(
        status: ProductStatus? = null,
        sku: String = "TEST-${UUID.randomUUID()}"
    ): ProductEntity {
        val product = ProductEntity(
            sku = sku,
            name = "Test Product",
            description = "Test Description",
            price = BigDecimal("99.99"),
            quantity = 10,
            weight = 1.5,
            isAvailable = true,
            status = status,
            category = ProductCategory.ELECTRONICS,
            tags = TagsWrapper(emptyList()),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return productRepository.save(product)
    }

    private fun assertStatus(productId: UUID, expected: ProductStatus) {
        val product = productRepository.findById(productId).orElseThrow()
        assertEquals(expected, product.status)
    }
}