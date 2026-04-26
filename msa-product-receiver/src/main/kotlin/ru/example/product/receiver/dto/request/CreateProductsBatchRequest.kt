package ru.example.product.receiver.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

/**
 * Request DTO for creating multiple products in batch.
 */
@Schema(description = "Request to create multiple products in batch")
data class CreateProductsBatchRequest(
    @field:NotEmpty(message = "At least one product is required")
    @field:Size(max = 100, message = "Batch size cannot exceed 100 products")
    @field:Valid
    @Schema(
        description = "List of products to create",
        required = true,
    )
    val products: List<CreateProductRequest>,
)
