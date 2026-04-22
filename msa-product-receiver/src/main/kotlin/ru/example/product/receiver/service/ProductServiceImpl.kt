package ru.example.product.receiver.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.example.product.receiver.domain.ProductCategory
import ru.example.product.receiver.domain.ProductDto
import ru.example.product.receiver.domain.ProductEntity
import ru.example.product.receiver.dto.request.CreateProductRequest
import ru.example.product.receiver.dto.request.UpdateProductRequest
import ru.example.product.receiver.exception.ProductNotFoundException
import ru.example.product.receiver.repository.ProductRepository
import java.time.Instant
import java.util.*

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository
) : ProductService {

    @Transactional
    override fun createProduct(request: CreateProductRequest): ProductDto {
        // Check if SKU already exists
        if (productRepository.existsBySku(request.sku)) {
            throw IllegalArgumentException("Product with SKU '${request.sku}' already exists")
        }

        val entity = ProductEntity(
            sku = request.sku,
            name = request.name,
            description = request.description,
            price = request.price,
            quantity = request.quantity,
            weight = request.weight,
            isAvailable = request.isAvailable,
            category = ProductCategory.valueOf(request.category.uppercase()),
            tags = request.tags,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val savedEntity = productRepository.save(entity)
        return toDto(savedEntity)
    }

    @Transactional(readOnly = true)
    override fun getProduct(id: UUID): ProductDto {
        val entity = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }
        return toDto(entity)
    }

    @Transactional(readOnly = true)
    override fun getAllProducts(): List<ProductDto> {
        return productRepository.findAllByOrderByCreatedAtDesc()
            .map { toDto(it) }
    }

    @Transactional
    override fun updateProduct(id: UUID, request: UpdateProductRequest): ProductDto {
        val existingEntity = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }

        val updatedEntity = existingEntity.copy(
            sku = request.sku ?: existingEntity.sku,
            name = request.name ?: existingEntity.name,
            description = request.description ?: existingEntity.description,
            price = request.price ?: existingEntity.price,
            quantity = request.quantity ?: existingEntity.quantity,
            weight = request.weight ?: existingEntity.weight,
            isAvailable = request.isAvailable ?: existingEntity.isAvailable,
            category = request.category?.let { ProductCategory.valueOf(it.uppercase()) } ?: existingEntity.category,
            tags = request.tags ?: existingEntity.tags,
            updatedAt = Instant.now()
        )

        // Check SKU uniqueness if changed
        if (request.sku != null && request.sku != existingEntity.sku) {
            if (productRepository.existsBySku(request.sku)) {
                throw IllegalArgumentException("Product with SKU '${request.sku}' already exists")
            }
        }

        val savedEntity = productRepository.save(updatedEntity)
        return toDto(savedEntity)
    }

    @Transactional
    override fun deleteProduct(id: UUID) {
        if (!productRepository.existsById(id)) {
            throw ProductNotFoundException(id)
        }
        productRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun existsBySku(sku: String): Boolean {
        return productRepository.existsBySku(sku)
    }

    private fun toDto(entity: ProductEntity): ProductDto {
        return ProductDto(
            id = entity.id,
            sku = entity.sku,
            name = entity.name,
            description = entity.description,
            price = entity.price,
            quantity = entity.quantity,
            weight = entity.weight,
            isAvailable = entity.isAvailable,
            category = entity.category,
            tags = entity.tags,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}