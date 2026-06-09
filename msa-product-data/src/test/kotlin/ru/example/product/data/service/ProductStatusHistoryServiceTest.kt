package ru.example.product.data.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.domain.ProductStatusHistoryEntity
import ru.example.product.data.dto.request.StatusHistoryQueryRequest
import ru.example.product.data.mappers.ProductStatusHistoryMapper
import ru.example.product.data.repository.ProductStatusHistoryRepository
import java.time.Instant
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ProductStatusHistoryServiceTest {

    @Mock
    private lateinit var productStatusHistoryRepository: ProductStatusHistoryRepository

    @Mock
    private lateinit var productStatusHistoryMapper: ProductStatusHistoryMapper

    @InjectMocks
    private lateinit var productStatusHistoryService: ProductStatusHistoryServiceImpl

    @Test
    fun `getStatusHistory should return records with default parameters`() {
        val mockEntity = createMockEntity()
        val mockDto = createMockDto()

        whenever(productStatusHistoryRepository.findTopByOrderByCreatedAtDesc(1000))
            .thenReturn(listOf(mockEntity))
        whenever(productStatusHistoryMapper.toDto(listOf(mockEntity)))
            .thenReturn(listOf(mockDto))

        val query = StatusHistoryQueryRequest()
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(1, result.size)
        assertNotNull(result[0].id)
    }

    @Test
    fun `getStatusHistory should return records with createdAfter parameter`() {
        val mockEntity = createMockEntity()
        val mockDto = createMockDto()
        val createdAfter = Instant.now().minusSeconds(3600)

        whenever(
            productStatusHistoryRepository.findByCreatedAtAfterOrderByCreatedAtDesc(createdAfter, 100)
        )
            .thenReturn(listOf(mockEntity))
        whenever(productStatusHistoryMapper.toDto(listOf(mockEntity)))
            .thenReturn(listOf(mockDto))

        val query = StatusHistoryQueryRequest(createdAfter = createdAfter, limit = 100)
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(1, result.size)
    }

    @Test
    fun `getStatusHistory should return records with productId parameter`() {
        val mockEntity = createMockEntity()
        val mockDto = createMockDto()
        val productId = UUID.randomUUID()

        whenever(
            productStatusHistoryRepository.findByProductIdOrderByCreatedAtDesc(productId, 50)
        )
            .thenReturn(listOf(mockEntity))
        whenever(productStatusHistoryMapper.toDto(listOf(mockEntity)))
            .thenReturn(listOf(mockDto))

        val query = StatusHistoryQueryRequest(productId = productId.toString(), limit = 50)
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(1, result.size)
    }

    @Test
    fun `getStatusHistory should return records with toStatus parameter`() {
        val mockEntity = createMockEntity()
        val mockDto = createMockDto()

        whenever(
            productStatusHistoryRepository.findByCreatedAtAfterAndToStatusOrderByCreatedAtDesc(
                Instant.EPOCH,
                "ACTIVE",
                1000,
            )
        )
            .thenReturn(listOf(mockEntity))
        whenever(productStatusHistoryMapper.toDto(listOf(mockEntity)))
            .thenReturn(listOf(mockDto))

        val query = StatusHistoryQueryRequest(toStatus = "ACTIVE")
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(1, result.size)
    }

    @Test
    fun `getStatusHistory should return records with fromStatus parameter`() {
        val mockEntity = createMockEntity()
        val mockDto = createMockDto()

        whenever(productStatusHistoryRepository.findByFromStatusOrderByCreatedAtDesc("DRAFT", 100))
            .thenReturn(listOf(mockEntity))
        whenever(productStatusHistoryMapper.toDto(listOf(mockEntity)))
            .thenReturn(listOf(mockDto))

        val query = StatusHistoryQueryRequest(fromStatus = "DRAFT", limit = 100)
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(1, result.size)
    }

    @Test
    fun `getStatusHistory should return records with productId and toStatus parameters`() {
        val mockEntity = createMockEntity()
        val mockDto = createMockDto()
        val productId = UUID.randomUUID()

        whenever(
            productStatusHistoryRepository.findByProductIdAndToStatusOrderByCreatedAtDesc(
                productId,
                "ACTIVE",
                50,
            )
        )
            .thenReturn(listOf(mockEntity))
        whenever(productStatusHistoryMapper.toDto(listOf(mockEntity)))
            .thenReturn(listOf(mockDto))

        val query = StatusHistoryQueryRequest(
            productId = productId.toString(),
            toStatus = "ACTIVE",
            limit = 50,
        )
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(1, result.size)
    }

    @Test
    fun `getStatusHistory should return empty list when repository returns empty list`() {
        whenever(productStatusHistoryRepository.findTopByOrderByCreatedAtDesc(1000))
            .thenReturn(emptyList())
        whenever(productStatusHistoryMapper.toDto(emptyList()))
            .thenReturn(emptyList())

        val query = StatusHistoryQueryRequest()
        val result = productStatusHistoryService.getStatusHistory(query)

        assertEquals(0, result.size)
    }

    private fun createMockEntity(): ProductStatusHistoryEntity {
        return ProductStatusHistoryEntity(
            id = UUID.randomUUID(),
            productId = UUID.randomUUID(),
            fromStatus = "DRAFT",
            toStatus = "PENDING_REVIEW",
            reason = "Test reason",
            userId = UUID.randomUUID(),
            createdAt = Instant.now(),
            processingDurationSeconds = 123L,
        )
    }

    private fun createMockDto(): ProductStatusHistoryDto {
        return ProductStatusHistoryDto(
            id = UUID.randomUUID().toString(),
            productId = UUID.randomUUID().toString(),
            fromStatus = "DRAFT",
            toStatus = "PENDING_REVIEW",
            reason = "Test reason",
            userId = UUID.randomUUID().toString(),
            createdAt = Instant.now(),
            processingDurationSeconds = 123L,
        )
    }
}
