package ru.example.product.receiver.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.math.BigDecimal

/**
 * Request DTO for updating an existing product.
 */
@Schema(description = "Request to update an existing product")
data class UpdateProductRequest(
    @field:Size(min = 1, max = 50, message = "SKU must be between 1 and 50 characters")
    @Schema(description = "Stock keeping unit (article number)", example = "SKU-12345", nullable = true)
    val sku: String? = null,

    @field:Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Schema(description = "Product name", example = "Laptop Pro", nullable = true)
    val name: String? = null,

    @Schema(description = "Product description", example = "Updated description", nullable = true)
    val description: String? = null,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Schema(description = "Price in currency", example = "1499.99", nullable = true)
    val price: BigDecimal? = null,

    @field:Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "Quantity in stock", example = "15", nullable = true)
    val quantity: Int? = null,

    @Schema(description = "Weight in kilograms", example = "2.7", nullable = true)
    val weight: Double? = null,

    @Schema(description = "Availability flag", example = "false", nullable = true)
    val isAvailable: Boolean? = null,

    @Schema(description = "Product category", example = "ELECTRONICS", nullable = true)
    val category: String? = null,

    @Schema(description = "List of tags", example = "[\"electronics\", \"premium\"]", nullable = true)
    val tags: List<String>? = null
)