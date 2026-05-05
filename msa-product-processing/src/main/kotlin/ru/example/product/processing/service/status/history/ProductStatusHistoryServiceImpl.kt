package ru.example.product.processing.service.status.history

import org.springframework.stereotype.Service
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.repository.ProductStatusHistoryRepository
import java.util.UUID
import ru.example.product.processing.domain.status.ProductStatusHistoryRecord as DomainRecord

@Service
class ProductStatusHistoryServiceImpl(
    private val repository: ProductStatusHistoryRepository,
) : ProductStatusHistoryService {
    override fun recordTransition(
        productId: UUID,
        from: ProductStatus,
        to: ProductStatus,
        reason: String?,
        userId: UUID?,
    ) {
        val record =
            DomainRecord(
                productId = productId,
                fromStatus = from,
                toStatus = to,
                reason = reason,
                userId = userId,
            )
        repository.save(record)
    }

    override fun getHistory(productId: UUID): List<ProductStatusHistoryRecord> {
        return repository.findAllByProductIdOrderByTimestampAsc(productId)
            .map { domainRecord ->
                ProductStatusHistoryRecord(
                    id = domainRecord.id!!,
                    productId = domainRecord.productId,
                    fromStatus = domainRecord.fromStatus,
                    toStatus = domainRecord.toStatus,
                    timestamp = domainRecord.timestamp,
                    userId = domainRecord.userId,
                    reason = domainRecord.reason,
                )
            }
    }
}
