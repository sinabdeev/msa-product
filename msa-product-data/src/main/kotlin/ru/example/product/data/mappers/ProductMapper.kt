package ru.example.product.data.mappers

import org.springframework.stereotype.Component
import ru.example.product.data.domain.ProductDto
import ru.example.product.data.domain.ProductEntity

@Component
class ProductMapper {
    fun toDto(entity: ProductEntity): ProductDto {
        return ProductDto(
            id = entity.id,
            sku = entity.sku,
            name = entity.name,
            description = entity.description,
            price = entity.price,
            quantity = entity.quantity,
            weight = entity.weight,
            isAvailable = entity.isAvailable,
            category = entity.category,
            tags = entity.tags.values,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}
