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
    @Query(
        "SELECT * FROM product_status_history WHERE created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByCreatedAtAfterOrderByCreatedAtDesc(
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после timestamp для конкретного продукта.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
        productId: UUID,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после timestamp с фильтром по to_status.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE created_at >= :createdAfter AND to_status = :toStatus ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByCreatedAtAfterAndToStatusOrderByCreatedAtDesc(
        createdAfter: Instant,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта.
     */
    @Query("SELECT * FROM product_status_history WHERE product_id = :productId ORDER BY created_at DESC LIMIT :limit")
    fun findByProductIdOrderByCreatedAtDesc(
        productId: UUID,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по to_status.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND to_status = :toStatus ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndToStatusOrderByCreatedAtDesc(
        productId: UUID,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status (для графика "Откуда переходят").
     */
    @Query("SELECT * FROM product_status_history WHERE from_status = :fromStatus ORDER BY created_at DESC LIMIT :limit")
    fun findByFromStatusOrderByCreatedAtDesc(
        fromStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status и created_after.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE from_status = :fromStatus AND created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByFromStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        fromStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status и to_status.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE from_status = :fromStatus AND to_status = :toStatus ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByFromStatusAndToStatusOrderByCreatedAtDesc(
        fromStatus: String,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по from_status, to_status и created_after.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE from_status = :fromStatus AND to_status = :toStatus AND created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByFromStatusAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        fromStatus: String,
        toStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по from_status, to_status и created_after.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND from_status = :fromStatus AND to_status = :toStatus AND created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndFromStatusAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        productId: UUID,
        fromStatus: String,
        toStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по from_status и to_status.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND from_status = :fromStatus AND to_status = :toStatus ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndFromStatusAndToStatusOrderByCreatedAtDesc(
        productId: UUID,
        fromStatus: String,
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по to_status и created_after.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND to_status = :toStatus AND created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndToStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        productId: UUID,
        toStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по from_status и created_after.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND from_status = :fromStatus AND created_at >= :createdAfter ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndFromStatusAndCreatedAtAfterOrderByCreatedAtDesc(
        productId: UUID,
        fromStatus: String,
        createdAfter: Instant,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по from_status.
     */
    @Query(
        "SELECT * FROM product_status_history WHERE product_id = :productId AND from_status = :fromStatus ORDER BY created_at DESC LIMIT :limit",
    )
    fun findByProductIdAndFromStatusOrderByCreatedAtDesc(
        productId: UUID,
        fromStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи с фильтром по to_status.
     */
    @Query("SELECT * FROM product_status_history WHERE to_status = :toStatus ORDER BY created_at DESC LIMIT :limit")
    fun findByToStatusOrderByCreatedAtDesc(
        toStatus: String,
        limit: Int,
    ): List<ProductStatusHistoryEntity>
}
