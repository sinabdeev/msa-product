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

        // Step 1: Get first M products (M = 15-30)
        val mProducts = fetchFirstMProducts()
        logger.info("Fetched {} products (M)", mProducts.size)

        // Step 2: Random selection of N products from M (N = 2-10)
        val selectedProducts = randomSelection(mProducts)
        logger.info("Randomly selected {} products (N) from {}", selectedProducts.size, mProducts.size)

        // Step 3: Process selected products
        val processedProducts =
            selectedProducts.map { product ->
                processProduct(product)
            }

        logger.info("Finished batch product processing. Processed {} products", processedProducts.size)
        return processedProducts
    }

    /**
     * Fetch first M products from the repository, where M is a random value between 15 and 30.
     * @return List of M products sorted by created_at descending
     */
    private fun fetchFirstMProducts(): List<ProductEntity> {
        val m = (15..30).random() // range [15, 30]
        return productRepository.findFirstByOrderByCreatedAtDesc(m)
    }

    /**
     * Perform random selection of N products from the given list, where N is a random value between 2 and 10.
     * @param products Input list of products
     * @return List of N randomly selected products
     */
    private fun randomSelection(products: List<ProductEntity>): List<ProductEntity> {
        val n = minOf((2..10).random(), products.size) // range [2, 10], capped by available products
        val shuffled = products.shuffled()
        return shuffled.take(n)
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
