package ru.example.product.processing.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.example.product.processing.domain.status.ProductStatusHistoryRecord
import java.util.UUID

/**
 * Репозиторий для записей истории изменений статуса продукта.
 */
@Repository
interface ProductStatusHistoryRepository : CrudRepository<ProductStatusHistoryRecord, UUID> {
    /**
     * Находит все записи истории для указанного продукта, отсортированные по времени (от старых к новым).
     * @param productId идентификатор продукта
     * @return список записей истории
     */
    fun findAllByProductIdOrderByTimestampAsc(productId: UUID): List<ProductStatusHistoryRecord>

    /**
     * Находит последнюю запись истории для указанного продукта (самую свежую).
     * @param productId идентификатор продукта
     * @return последняя запись или null, если записей нет
     */
    fun findTopByProductIdOrderByTimestampDesc(productId: UUID): ProductStatusHistoryRecord?
}
