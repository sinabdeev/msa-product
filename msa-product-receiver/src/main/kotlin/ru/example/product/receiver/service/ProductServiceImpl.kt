package ru.example.product.receiver.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.example.product.receiver.domain.ProductCategory
import ru.example.product.receiver.domain.ProductDto
import ru.example.product.receiver.domain.ProductEntity
import ru.example.product.receiver.domain.TagsWrapper
import ru.example.product.receiver.dto.request.CreateProductRequest
import ru.example.product.receiver.dto.request.UpdateProductRequest
import ru.example.product.receiver.exception.ProductNotFoundException
import ru.example.product.receiver.repository.ProductRepository
import java.time.Instant
import java.util.*

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
) : ProductService {
    private val logger: Logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    @Transactional
    override fun createProduct(request: CreateProductRequest): ProductDto {
        logger.info("Creating product with SKU: {}", request.sku)

        // Check if SKU already exists
        if (productRepository.existsBySku(request.sku)) {
            logger.warn("Product with SKU '{}' already exists", request.sku)
            throw IllegalArgumentException("Product with SKU '${request.sku}' already exists")
        }

        val entity =
            ProductEntity(
                sku = request.sku,
                name = request.name,
                description = request.description,
                price = request.price,
                quantity = request.quantity,
                weight = request.weight,
                isAvailable = request.isAvailable,
                category = ProductCategory.valueOf(request.category.uppercase()),
                tags = TagsWrapper(request.tags),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            )

        val savedEntity = productRepository.save(entity)
        logger.info("Product created successfully with ID: {}", savedEntity.id)
        return toDto(savedEntity)
    }

    @Transactional
    override fun createProductsBatch(requests: List<CreateProductRequest>): List<ProductDto> {
        logger.info("Creating batch of {} products", requests.size)

        // Validate batch size
        if (requests.size > 100) {
            logger.warn("Batch size {} exceeds maximum limit of 100", requests.size)
            throw IllegalArgumentException("Batch size cannot exceed 100 products")
        }

        // Check for duplicate SKUs within the batch
        val skuSet = mutableSetOf<String>()
        val duplicateSkus = mutableListOf<String>()
        requests.forEach { request ->
            if (!skuSet.add(request.sku)) {
                duplicateSkus.add(request.sku)
            }
        }
        if (duplicateSkus.isNotEmpty()) {
            logger.warn("Duplicate SKUs found in batch: {}", duplicateSkus)
            throw IllegalArgumentException("Duplicate SKUs in batch: ${duplicateSkus.joinToString()}")
        }

        // Check for SKU conflicts with existing products
        val conflictingSkus = mutableListOf<String>()
        requests.forEach { request ->
            if (productRepository.existsBySku(request.sku)) {
                conflictingSkus.add(request.sku)
            }
        }
        if (conflictingSkus.isNotEmpty()) {
            logger.warn("SKU conflicts with existing products: {}", conflictingSkus)
            throw IllegalArgumentException("SKU already exists: ${conflictingSkus.joinToString()}")
        }

        // Create all entities
        val now = Instant.now()
        val entities =
            requests.map { request ->
                ProductEntity(
                    sku = request.sku,
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    quantity = request.quantity,
                    weight = request.weight,
                    isAvailable = request.isAvailable,
                    category = ProductCategory.valueOf(request.category.uppercase()),
                    tags = TagsWrapper(request.tags),
                    createdAt = now,
                    updatedAt = now,
                )
            }

        // Save all entities
        val savedEntities = productRepository.saveAll(entities)
        logger.info("Batch created successfully with {} products", savedEntities.count())

        // Convert to DTOs
        return savedEntities.map { toDto(it) }
    }

    @Transactional(readOnly = true)
    override fun getProduct(id: UUID): ProductDto {
        logger.debug("Getting product with ID: {}", id)
        val entity =
            productRepository.findById(id)
                .orElseThrow {
                    logger.warn("Product not found with ID: {}", id)
                    ProductNotFoundException(id)
                }
        return toDto(entity)
    }

    @Transactional(readOnly = true)
    override fun getAllProducts(): List<ProductDto> {
        return productRepository.findAllByOrderByCreatedAtDesc()
            .map { toDto(it) }
    }

    @Transactional
    override fun updateProduct(
        id: UUID,
        request: UpdateProductRequest,
    ): ProductDto {
        logger.info("Updating product with ID: {}", id)

        val existingEntity =
            productRepository.findById(id)
                .orElseThrow {
                    logger.warn("Product not found for update with ID: {}", id)
                    ProductNotFoundException(id)
                }

        val updatedEntity =
            existingEntity.copy(
                sku = request.sku ?: existingEntity.sku,
                name = request.name ?: existingEntity.name,
                description = request.description ?: existingEntity.description,
                price = request.price ?: existingEntity.price,
                quantity = request.quantity ?: existingEntity.quantity,
                weight = request.weight ?: existingEntity.weight,
                isAvailable = request.isAvailable ?: existingEntity.isAvailable,
                category = request.category?.let { ProductCategory.valueOf(it.uppercase()) } ?: existingEntity.category,
                tags = request.tags ?.let { TagsWrapper(request.tags) } ?: existingEntity.tags,
                updatedAt = Instant.now(),
            )

        // Check SKU uniqueness if changed
        if (request.sku != null && request.sku != existingEntity.sku) {
            if (productRepository.existsBySku(request.sku)) {
                logger.warn("Product with SKU '{}' already exists during update", request.sku)
                throw IllegalArgumentException("Product with SKU '${request.sku}' already exists")
            }
        }

        val savedEntity = productRepository.save(updatedEntity)
        logger.info("Product updated successfully with ID: {}", id)
        return toDto(savedEntity)
    }

    @Transactional
    override fun deleteProduct(id: UUID) {
        logger.info("Deleting product with ID: {}", id)
        if (!productRepository.existsById(id)) {
            logger.warn("Product not found for deletion with ID: {}", id)
            throw ProductNotFoundException(id)
        }
        productRepository.deleteById(id)
        logger.info("Product deleted successfully with ID: {}", id)
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
            tags = entity.tags.values,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }
}
