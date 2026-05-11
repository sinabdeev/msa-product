package ru.example.product.processing.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import ru.example.product.processing.domain.status.StatusHistoryEntity
import java.util.UUID

interface StatusHistoryRepository : CrudRepository<StatusHistoryEntity, UUID> {
    @Query("SELECT * FROM product_status_history WHERE product_id = :productId ORDER BY created_at DESC")
    fun findByProductId(
        @Param("productId") productId: UUID,
    ): List<StatusHistoryEntity>
}
