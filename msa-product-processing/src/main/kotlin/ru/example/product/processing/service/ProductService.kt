package ru.example.product.processing.service

import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
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

    /**
     * Update product status.
     * @param productId Product ID
     * @param targetStatus Target status
     * @param reason Optional reason for the transition
     * @return Updated product entity
     */
    fun updateStatus(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String? = null,
    ): ProductEntity

    /**
     * Get possible status transitions for a product.
     * @param productId Product ID
     * @return Set of statuses that can be transitioned to from the current status
     */
    fun getPossibleTransitions(productId: UUID): Set<ProductStatus>
}
