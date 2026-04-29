package ru.example.product.generator.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.example.product.generator.domain.ProductEntity
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
}
