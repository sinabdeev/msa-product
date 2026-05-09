package ru.example.product.processing.repository

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.example.product.processing.domain.ProductEntity
import java.util.*

/**
 * Repository for product entities using Spring Data JDBC.
 */
@Repository
interface ProductRepository : CrudRepository<ProductEntity, UUID> {
    /**
     * Check if a product with given SKU exists.
     */
    fun existsBySku(sku: String): Boolean

    /**
     * Find first M products ordered by creation date descending, excluding ARCHIVED status.
     * Includes products with NULL status.
     * @param count Maximum number of products to return
     */
    @Query(
        " " +
            " SELECT * FROM products " +
            " WHERE status IS NULL OR status != 'ARCHIVED' " +
            " ORDER BY created_at " +
            " LIMIT :count ",
    )
    fun getActiveProducts(count: Int): List<ProductEntity>
}
