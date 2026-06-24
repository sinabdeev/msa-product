package com.example.msaproductviewmobile.domain.model

/**
 * Domain model representing a product.
 */
data class Product(
    val id: String,
    val name: String,
    val status: ProductStatus,
    val lastUpdated: Long,
    val description: String? = null
)

/**
 * Product status enum.
 */
enum class ProductStatus {
    ACTIVE,
    INACTIVE,
    PENDING,
    ERROR,
    UNKNOWN
}
