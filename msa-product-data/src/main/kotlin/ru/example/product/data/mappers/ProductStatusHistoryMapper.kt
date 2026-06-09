package ru.example.product.data.mappers

import org.springframework.stereotype.Component
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.domain.ProductStatusHistoryEntity

/**
 * Mapper for ProductStatusHistoryEntity <-> ProductStatusHistoryDto transformation.
 */
@Component
class ProductStatusHistoryMapper {

    fun toDto(entity: ProductStatusHistoryEntity): ProductStatusHistoryDto {
        return ProductStatusHistoryDto(
            id = entity.id?.toString() ?: "",
            productId = entity.productId.toString(),
            fromStatus = entity.fromStatus,
            toStatus = entity.toStatus,
            reason = entity.reason,
            userId = entity.userId?.toString(),
            createdAt = entity.createdAt,
            processingDurationSeconds = entity.processingDurationSeconds,
        )
    }

    fun toDto(entities: List<ProductStatusHistoryEntity>): List<ProductStatusHistoryDto> {
        return entities.map(::toDto)
    }
}
