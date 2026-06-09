package ru.example.product.data.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.dto.request.StatusHistoryQueryRequest
import ru.example.product.data.dto.response.ApiResponse
import ru.example.product.data.service.ProductStatusHistoryService
import java.time.Instant

/**
 * REST controller for product status history API.
 */
@RestController
@RequestMapping("/api/v1/status-history")
@Tag(name = "Status History", description = "Product status history API")
class StatusHistoryController(
    private val productStatusHistoryService: ProductStatusHistoryService,
) {

    @Operation(summary = "Get product status history records")
    @SwaggerApiResponse(
        responseCode = "200",
        description = "Status history retrieved successfully",
        content = [
            Content(
                mediaType = "application/json",
                schema = Schema(implementation = ApiResponse::class),
            ),
        ],
    )
    @GetMapping
    fun getStatusHistory(
        @Parameter(description = "Максимальное количество записей", example = "1000")
        @RequestParam(defaultValue = "1000") limit: Int,

        @Parameter(description = "Возвращать записи после этого timestamp", example = "2026-06-03T20:19:45")
        @RequestParam(name = "created_after", required = false) createdAfter: String?,

        @Parameter(description = "ID продукта для фильтрации", example = "123e4567-e89b-12d3-a456-426614174002")
        @RequestParam(required = false) productId: String?,

        @Parameter(description = "Фильтр по статусу после перехода", example = "ACTIVE")
        @RequestParam(required = false) toStatus: String?,

        @Parameter(description = "Фильтр по статусу до перехода", example = "DRAFT")
        @RequestParam(required = false) fromStatus: String?,
    ): ResponseEntity<ApiResponse<List<ProductStatusHistoryDto>>> {

        val createdAfterInstant = createdAfter?.let {
            try {
                Instant.parse(it)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid createdAfter format: $it. Expected ISO-8601 format.")
            }
        }

        val query = StatusHistoryQueryRequest(
            limit = limit,
            createdAfter = createdAfterInstant,
            productId = productId,
            toStatus = toStatus,
            fromStatus = fromStatus,
        )

        val records = productStatusHistoryService.getStatusHistory(query)
        val response = ApiResponse.success("Status history retrieved successfully", records)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
