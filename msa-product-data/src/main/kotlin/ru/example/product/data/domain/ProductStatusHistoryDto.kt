package ru.example.product.data.domain

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

/**
 * DTO for product status history records.
 * Used for API responses to the frontend.
 */
@Schema(description = "Product status history record")
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
data class ProductStatusHistoryDto(
    @Schema(description = "ID записи истории", example = "123e4567-e89b-12d3-a456-426614174001")
    val id: String,
    @Schema(description = "ID продукта", example = "123e4567-e89b-12d3-a456-426614174002")
    val productId: String,
    @Schema(description = "Статус до перехода", example = "DRAFT")
    val fromStatus: String,
    @Schema(description = "Статус после перехода", example = "PENDING_REVIEW")
    val toStatus: String,
    @Schema(description = "Причина перехода", example = "Batch processing")
    val reason: String?,
    @Schema(description = "ID пользователя", example = "123e4567-e89b-12d3-a456-426614174003")
    val userId: String?,
    @Schema(description = "Время создания записи", example = "2026-06-03T20:19:45.000Z")
    val createdAt: Instant,
    @Schema(description = "Длительность обработки в секундах", example = "12345")
    val processingDurationSeconds: Long?,
)
