package ru.example.product.receiver.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.Instant
import java.util.*

/**
 * Product Data Transfer Object for API responses.
 * Matches the specification from TASK-01.
 */
@Schema(description = "Product data transfer object")
data class ProductDto(
    @Schema(description = "Unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: UUID?,
    @Schema(description = "Stock keeping unit (article number)", example = "SKU-12345")
    val sku: String,
    @Schema(description = "Product name", example = "Laptop")
    val name: String,
    @Schema(description = "Product description", example = "High-performance laptop with 16GB RAM")
    val description: String,
    @Schema(description = "Price in currency", example = "1299.99")
    val price: BigDecimal,
    @Schema(description = "Quantity in stock", example = "10")
    val quantity: Int,
    @Schema(description = "Weight in kilograms", example = "2.5", nullable = true)
    val weight: Double?,
    @Schema(description = "Availability flag", example = "true")
    val isAvailable: Boolean,
    @Schema(description = "Product category", example = "ELECTRONICS")
    val category: ProductCategory,
    @Schema(description = "List of tags", example = "[\"electronics\", \"laptop\", \"gaming\"]")
    val tags: List<String>,
    @Schema(description = "Creation timestamp", example = "2023-01-01T12:00:00Z", nullable = true)
    val createdAt: Instant?,
    @Schema(description = "Last update timestamp", example = "2023-01-02T14:30:00Z", nullable = true)
    val updatedAt: Instant?,
)
