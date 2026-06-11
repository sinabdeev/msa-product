package ru.example.product.data.dto.request

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant

/**
 * Query parameters for status history search.
 */
@Schema(description = "Query parameters for status history search")
data class StatusHistoryQueryRequest(
    @Parameter(description = "Максимальное количество записей для возврата", example = "1000")
    @Schema(defaultValue = "1000")
    @Min(1)
    @Max(10000)
    val limit: Int = 1000,
    @Parameter(description = "Возвращать записи после этого timestamp (inclusive)")
    @Schema(example = "2026-06-03T20:19:45")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val createdAfter: Instant? = null,
    @Parameter(description = "ID продукта для фильтрации")
    @Schema(example = "123e4567-e89b-12d3-a456-426614174002")
    val productId: String? = null,
    @Parameter(description = "Фильтр по статусу после перехода")
    @Schema(example = "ACTIVE")
    val toStatus: String? = null,
    @Parameter(description = "Фильтр по статусу до перехода")
    @Schema(example = "DRAFT")
    val fromStatus: String? = null,
)
