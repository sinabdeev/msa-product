package ru.example.product.generator.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import ru.example.product.generator.domain.ProductCategory
import ru.example.product.generator.domain.ProductDto
import ru.example.product.generator.domain.ProductEntity
import ru.example.product.generator.domain.TagsWrapper
import ru.example.product.generator.dto.request.CreateProductRequest
import ru.example.product.generator.dto.response.BatchSaveResult
import ru.example.product.generator.mappers.ProductMapper
import ru.example.product.generator.repository.ProductRepository
import java.time.Instant
import java.util.concurrent.CompletableFuture

@Component
class ProductHandler(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
) {
    private val logger: Logger = LoggerFactory.getLogger(ProductHandler::class.java)

    /**
     * Saves a single product asynchronously.
     * This method is marked with @Async and will be executed in the productSaveThreadPool.
     *
     * @param request Create product request
     * @param index Original index of the product in the batch request
     * @return CompletableFuture with BatchSaveResult
     */
    @Async("productSaveThreadPool")
    fun saveProductAsync(
        request: CreateProductRequest,
        index: Int,
    ): CompletableFuture<BatchSaveResult<ProductDto>> {
        return try {
            logger.debug(
                "Async saving product {}: {} in thread {}",
                index,
                request.sku,
                Thread.currentThread().name,
            )

            Thread.sleep((50 + (333 * Math.random()).toInt()).toLong())

            // Check if SKU already exists (double-check in case of race conditions)
            if (productRepository.existsBySku(request.sku)) {
                val error = "Product with SKU '${request.sku}' already exists"
                logger.warn(error)
                CompletableFuture.completedFuture(
                    BatchSaveResult(
                        success = false,
                        error = error,
                        index = index,
                    ),
                )
            } else {
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
                logger.debug("Product saved successfully: {}", savedEntity.sku)
                CompletableFuture.completedFuture(
                    BatchSaveResult(
                        success = true,
                        data = productMapper.toDto(savedEntity),
                        index = index,
                    ),
                )
            }
        } catch (e: Exception) {
            logger.error("Failed to save product {}: {}", request.sku, e.message, e)
            CompletableFuture.completedFuture(
                BatchSaveResult(
                    success = false,
                    error = "Failed to save product ${request.sku}: ${e.message}",
                    index = index,
                ),
            )
        }
    }
}
