package ru.example.product.data.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.example.product.data.domain.ProductStatusHistoryEntity
import java.time.Instant
import java.util.*

/**
 * Repository for product status history entity using Spring Data JDBC.
 */
@Repository
interface ProductStatusHistoryRepository : CrudRepository<ProductStatusHistoryEntity, UUID> {

    /**
     * Найти последние N записей, отсортированные по времени создания (новые первые).
     */
    @Query("SELECT * FROM product_status_history ORDER BY created_at DESC LIMIT :limit")
    fun findTopByOrderByCreatedAtDesc(limit: Int): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после указанного timestamp.
     */
    fun findByCreatedAtAfterOrderByCreatedAtDesc(createdAfter: Instant, limit: Int): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после timestamp для конкретного продукта.
     */
    fun findByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
        productId: UUID,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после timestamp с фильтром по to_status.
     */
    fun findByCreatedAtAfterAndToStatusOrderByCreatedAtDesc(
        createdAfter: Instant,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта.
     */
    fun findByProductIdOrderByCreatedAtDesc(productId: UUID, limit: Int): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по to_status.
     */
    fun findByProductIdAndToStatusOrderByCreatedAtDesc(
        productId: UUID,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status (для графика "Откуда переходят").
     */
    fun findByFromStatusOrderByCreatedAtDesc(
        fromStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status и created_after.
     */
    fun findByFromStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        fromStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status и to_status.
     */
    fun findByFromStatusAndToStatusOrderByCreatedAtDesc(
        fromStatus: String,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status, to_status и created_after.
     */
    fun findByFromStatusAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        fromStatus: String,
        toStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>
}
