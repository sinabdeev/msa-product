package ru.example.product.processing.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.exception.ProductNotFoundException
import ru.example.product.processing.repository.ProductRepository
import ru.example.product.processing.service.status.ProductStatusTransitionService
import java.util.*

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val statusTransitionService: ProductStatusTransitionService,
) : ProductService {
    private val logger: Logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

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

    @Transactional
    override fun updateStatus(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String?,
    ): ProductEntity {
        logger.info("Updating status for product {} to {} with reason: {}", productId, targetStatus, reason)
        return statusTransitionService.transitionProduct(productId, targetStatus, reason)
    }

    @Transactional(readOnly = true)
    override fun getPossibleTransitions(productId: UUID): Set<ProductStatus> {
        logger.debug("Getting possible transitions for product {}", productId)
        val product =
            productRepository.findById(productId)
                .orElseThrow { ProductNotFoundException(productId) }
        val currentStatus = product.status ?: ProductStatus.DRAFT
        return statusTransitionService.getAllowedTransitions(currentStatus)
    }

    /**
     * Process all products from the database.
     * @return List of processed product entities
     */
    override fun processProducts(): List<ProductEntity> {
        logger.info("Starting batch product processing")
        val products = productRepository.findAllByOrderByCreatedAtDesc()
        logger.info("Found {} products to process", products.size)

        val processedProducts =
            products.map { product ->
                processProduct(product)
            }

        logger.info("Finished batch product processing. Processed {} products", processedProducts.size)
        return processedProducts
    }

    /**
     * Process a single product (log id and status).
     * @param product Product entity to process
     * @return The processed product entity
     */
    private fun processProduct(product: ProductEntity): ProductEntity {
        val status = product.status?.name ?: ProductStatus.DRAFT.name
        logger.info("Processing product: id={}, status={}", product.id, status)
        return product
    }
}
