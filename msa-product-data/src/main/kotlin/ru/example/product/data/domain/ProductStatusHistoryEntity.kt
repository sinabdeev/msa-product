package ru.example.product.data.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

/**
 * Database entity for product status history records.
 * Maps to table: product.product_status_history
 */
@Table("product.product_status_history")
data class ProductStatusHistoryEntity(
    @Id
    val id: UUID? = null,

    @Column("product_id")
    val productId: UUID,

    @Column("from_status")
    val fromStatus: String,

    @Column("to_status")
    val toStatus: String,

    @Column("reason")
    val reason: String? = null,

    @Column("user_id")
    val userId: UUID? = null,

    @Column("created_at")
    val createdAt: Instant,

    @Column("processing_duration_seconds")
    val processingDurationSeconds: Long? = null,
)
