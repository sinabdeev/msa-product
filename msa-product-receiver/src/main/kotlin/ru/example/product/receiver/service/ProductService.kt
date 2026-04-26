package ru.example.product.receiver.service

import ru.example.product.receiver.domain.ProductDto
import ru.example.product.receiver.dto.request.CreateProductRequest
import ru.example.product.receiver.dto.request.UpdateProductRequest
import java.util.*

/**
 * Service interface for product operations.
 */
interface ProductService {
    /**
     * Create a new product.
     * @param request Create product request
     * @return Created product DTO
     */
    fun createProduct(request: CreateProductRequest): ProductDto

    /**
     * Create multiple products in a batch.
     * @param requests List of create product requests
     * @return List of created product DTOs
     * @throws IllegalArgumentException if any validation fails or SKU conflicts exist
     */
    fun createProductsBatch(requests: List<CreateProductRequest>): List<ProductDto>

    /**
     * Get product by ID.
     * @param id Product ID
     * @return Product DTO
     * @throws ru.example.product.receiver.exception.ProductNotFoundException if product not found
     */
    fun getProduct(id: UUID): ProductDto

    /**
     * Get all products.
     * @return List of product DTOs
     */
    fun getAllProducts(): List<ProductDto>

    /**
     * Update an existing product.
     * @param id Product ID
     * @param request Update product request
     * @return Updated product DTO
     * @throws ru.example.product.receiver.exception.ProductNotFoundException if product not found
     */
    fun updateProduct(
        id: UUID,
        request: UpdateProductRequest,
    ): ProductDto

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
