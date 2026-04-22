package ru.example.product.receiver.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.math.BigDecimal

/**
 * Request DTO for creating a new product.
 */
@Schema(description = "Request to create a new product")
data class CreateProductRequest(
    @field:NotBlank(message = "SKU is required")
    @field:Size(min = 1, max = 50, message = "SKU must be between 1 and 50 characters")
    @Schema(description = "Stock keeping unit (article number)", example = "SKU-12345", required = true)
    val sku: String,

    @field:NotBlank(message = "Name is required")
    @field:Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
    @Schema(description = "Product name", example = "Laptop", required = true)
    val name: String,

    @field:NotBlank(message = "Description is required")
    @Schema(description = "Product description", example = "High-performance laptop with 16GB RAM", required = true)
    val description: String,

    @field:NotNull(message = "Price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Schema(description = "Price in currency", example = "1299.99", required = true)
    val price: BigDecimal,

    @field:NotNull(message = "Quantity is required")
    @field:Min(value = 0, message = "Quantity cannot be negative")
    @Schema(description = "Quantity in stock", example = "10", required = true)
    val quantity: Int,

    @Schema(description = "Weight in kilograms", example = "2.5", nullable = true)
    val weight: Double? = null,

    @field:NotNull(message = "Availability flag is required")
    @Schema(description = "Availability flag", example = "true", required = true)
    val isAvailable: Boolean,

    @field:NotNull(message = "Category is required")
    @Schema(description = "Product category", example = "ELECTRONICS", required = true)
    val category: String,

    @Schema(description = "List of tags", example = "[\"electronics\", \"laptop\", \"gaming\"]")
    val tags: List<String> = emptyList()
)