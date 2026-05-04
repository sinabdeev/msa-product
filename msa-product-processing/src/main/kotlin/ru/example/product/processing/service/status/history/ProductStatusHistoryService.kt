package ru.example.product.processing.service.status.history

import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

/**
 * Сервис для ведения истории изменений статуса продукта.
 */
interface ProductStatusHistoryService {

    /**
     * Записывает факт перехода статуса продукта.
     * @param productId идентификатор продукта
     * @param from исходный статус
     * @param to целевой статус
     * @param reason причина перехода (опционально)
     * @param userId идентификатор пользователя, инициировавшего переход (опционально)
     */
    fun recordTransition(
        productId: UUID,
        from: ProductStatus,
        to: ProductStatus,
        reason: String? = null,
        userId: UUID? = null
    )

    /**
     * Возвращает историю переходов статуса для указанного продукта.
     * @param productId идентификатор продукта
     * @return список записей истории в хронологическом порядке (от старых к новым)
     */
    fun getHistory(productId: UUID): List<ProductStatusHistoryRecord>
}

/**
 * Запись об изменении статуса продукта (DTO для возврата из сервиса).
 */
data class ProductStatusHistoryRecord(
    val id: UUID,
    val productId: UUID,
    val fromStatus: ProductStatus,
    val toStatus: ProductStatus,
    val timestamp: java.time.Instant,
    val userId: UUID?,
    val reason: String?
)