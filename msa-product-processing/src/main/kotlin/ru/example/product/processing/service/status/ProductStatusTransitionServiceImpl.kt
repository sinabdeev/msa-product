package ru.example.product.processing.service.status

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.domain.status.StatusHistoryEntity
import ru.example.product.processing.exception.InvalidStatusTransitionException
import ru.example.product.processing.exception.ProductNotFoundException
import ru.example.product.processing.repository.ProductRepository
import ru.example.product.processing.repository.StatusHistoryRepository
import java.time.Instant
import java.util.UUID

@Service
class ProductStatusTransitionServiceImpl(
    private val productRepository: ProductRepository,
    private val validator: ProductStatusTransitionValidator,
    private val statusHistoryRepository: StatusHistoryRepository,
) : ProductStatusTransitionService {
    @Transactional
    override fun transitionProduct(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String?,
        userId: UUID?,
    ): ProductEntity {
        val product =
            productRepository.findById(productId)
                .orElseThrow { ProductNotFoundException(productId) }
        return transitionProduct(product, targetStatus, reason, userId)
    }

    @Transactional
    override fun transitionProduct(
        product: ProductEntity,
        targetStatus: ProductStatus,
        reason: String?,
        userId: UUID?,
    ): ProductEntity {
        val currentStatus = product.status ?: ProductStatus.DRAFT

        // Валидация перехода
        if (!validator.isTransitionAllowed(currentStatus, targetStatus)) {
            throw InvalidStatusTransitionException(
                productId = product.id,
                from = currentStatus,
                to = targetStatus,
            )
        }

        // Обновление продукта
        val updatedProduct =
            product.copy(
                status = targetStatus,
                updatedAt = Instant.now(),
            )

        val savedProduct = productRepository.save(updatedProduct)

        // Record status history
        val historyEntry =
            StatusHistoryEntity(
                productId = product.id!!,
                fromStatus = currentStatus.name,
                toStatus = targetStatus.name,
                reason = reason,
                userId = userId,
                createdAt = Instant.now(),
            )
        statusHistoryRepository.save(historyEntry)

        return savedProduct
    }

    override fun getAllowedTransitions(current: ProductStatus): Set<ProductStatus> {
        return validator.getAllowedTransitions(current)
    }
}
