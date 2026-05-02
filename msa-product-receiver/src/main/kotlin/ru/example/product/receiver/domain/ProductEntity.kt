package ru.example.product.receiver.domain

import org.springframework.data.annotation.Id
import org.springframework.data.jdbc.repository.config.*
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import ru.example.product.receiver.domain.ProductStatus
import java.util.*

/**
 * Database entity for products.
 * Uses Spring Data JDBC annotations.
 */
@Table("products")
data class ProductEntity(
    @Id
    val id: UUID? = null,
    @Column("sku")
    val sku: String,
    @Column("name")
    val name: String,
    @Column("description")
    val description: String,
    @Column("price")
    val price: BigDecimal,
    @Column("quantity")
    val quantity: Int,
    @Column("weight")
    val weight: Double? = null,
    @Column("is_available")
    val isAvailable: Boolean,
    @Column("status")
    val status: ProductStatus? = null,
    @Column("category")
    val category: ProductCategory,
    @Column("tags")
    val tags: TagsWrapper = TagsWrapper(emptyList()), // JSONB converter handles serialization
    @Column("created_at")
    val createdAt: Instant? = null,
    @Column("updated_at")
    val updatedAt: Instant? = null,
) {
    /**
     * Helper method to create a copy with updated timestamp.
     */
    fun withUpdatedAt(updatedAt: Instant): ProductEntity {
        return this.copy(updatedAt = updatedAt)
    }
}

data class TagsWrapper(val values: List<String>)
