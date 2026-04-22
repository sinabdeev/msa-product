package ru.example.product.receiver.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

/**
 * Standardized API response wrapper.
 * @param T Type of the data payload
 */
@Schema(description = "Standard API response wrapper")
data class ApiResponse<T>(
    @Schema(description = "Indicates if the request was successful", example = "true")
    val success: Boolean,

    @Schema(description = "Response message", example = "Operation completed successfully")
    val message: String,

    @Schema(description = "Response data payload")
    val data: T? = null,

    @Schema(description = "Timestamp of the response", example = "2023-01-01T12:00:00Z")
    val timestamp: Instant = Instant.now()
) {
    companion object {
        /**
         * Creates a successful response with data.
         */
        fun <T> success(message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = data)
        }

        /**
         * Creates a successful response without data.
         */
        fun <T> success(message: String): ApiResponse<T> {
            return ApiResponse(success = true, message = message, data = null)
        }

        /**
         * Creates an error response.
         */
        fun <T> error(message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(success = false, message = message, data = data)
        }
    }
}