package ru.example.product.receiver.dto.error

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

/**
 * Standard error response structure.
 */
@Schema(description = "Error response")
data class ApiError(
    @Schema(description = "HTTP status code", example = "400")
    val status: Int,
    @Schema(description = "Error message", example = "Validation failed")
    val message: String,
    @Schema(description = "Detailed error description", example = "SKU must be between 1 and 50 characters")
    val details: String? = null,
    @Schema(description = "Timestamp of the error", example = "2023-01-01T12:00:00Z")
    val timestamp: Instant = Instant.now(),
    @Schema(description = "Path where the error occurred", example = "/api/v1/products")
    val path: String? = null,
)
