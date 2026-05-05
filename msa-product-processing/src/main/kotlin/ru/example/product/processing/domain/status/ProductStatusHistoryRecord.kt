package ru.example.product.processing.domain.status

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.example.product.processing.domain.ProductStatus
import java.time.Instant
import java.util.UUID

/**
 * Запись об изменении статуса продукта.
 * Хранится в отдельной таблице для аудита и анализа.
 */
@Table("product_status_history")
data class ProductStatusHistoryRecord(
    @Id
    val id: UUID? = null,
    @Column("product_id")
    val productId: UUID,
    @Column("from_status")
    val fromStatus: ProductStatus,
    @Column("to_status")
    val toStatus: ProductStatus,
    @Column("timestamp")
    val timestamp: Instant = Instant.now(),
    @Column("user_id")
    val userId: UUID? = null,
    @Column("reason")
    val reason: String? = null,
)
