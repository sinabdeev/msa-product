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
     * Find product by SKU (case-sensitive).
     */
    fun findBySku(sku: String): ProductEntity?

    /**
     * Check if a product with given SKU exists.
     */
    fun existsBySku(sku: String): Boolean

    /**
     * Find all products ordered by creation date descending.
     */
    fun findAllByOrderByCreatedAtDesc(): List<ProductEntity>

    /**
     * Find first M products ordered by creation date descending with LIMIT.
     * @param count Maximum number of products to return
     */
    @Query("SELECT * FROM products ORDER BY created_at DESC LIMIT :count")
    fun findFirstByOrderByCreatedAtDesc(count: Int): List<ProductEntity>
}
