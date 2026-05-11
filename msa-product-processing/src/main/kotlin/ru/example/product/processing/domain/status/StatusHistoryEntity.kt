package ru.example.product.processing.domain.status

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

/**
 * Entity for tracking product status change history.
 */
@Table("product_status_history")
data class StatusHistoryEntity(
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
)
