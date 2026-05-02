package ru.example.product.processing.service

import java.util.*

/**
 * Service interface for product operations.
 */
interface ProductService {
    /**
     * Delete a product.
     * @param id Product ID
     */
    fun deleteProduct(id: UUID)

    /**
     * Check if product with given SKU exists.
     * @param sku Product SKU
     * @return true if exists
     */
    fun existsBySku(sku: String): Boolean
}
