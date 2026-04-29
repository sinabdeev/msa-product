package ru.example.product.generator.scheduler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.example.product.generator.client.ProductBatchClient
import ru.example.product.generator.generator.ProductGeneratorService

/**
 * Scheduled service that generates realistic random product data and submits it to the Product API.
 *
 * Runs every 60 seconds (fixedDelay), generating 10 products per execution
 * and submitting them asynchronously using the productGeneratorThreadPool.
 */
@Component
class ProductScheduler(
    private val productGeneratorService: ProductGeneratorService,
    private val productBatchClient: ProductBatchClient,
) {

    private companion object {
        private val log = LoggerFactory.getLogger(ProductScheduler::class.java)
        const val PRODUCTS_PER_BATCH = 10
    }

    /**
     * Scheduled task that runs every 60 seconds after completion.
     * Generates 10 random products and submits them to the API.
     */
    @Scheduled(fixedDelay = 60000, initialDelay = 5000)
    @Async("productGeneratorThreadPool")
    fun generateAndSubmitProducts() {
        log.info("Starting scheduled product generation task")

        try {
            val products = productGeneratorService.generateProducts(PRODUCTS_PER_BATCH)
            log.info("Generated $PRODUCTS_PER_BATCH products, submitting to API")

            val result = productBatchClient.submitBatch(products)

            result.ifPresent { response ->
                log.info("Successfully submitted batch. Response message: ${response.message}")
                response.data?.let { data ->
                    log.info("API returned ${data.size} created products")
                }
            }

            if (result.isEmpty) {
                log.warn("Batch submission returned no result - check API connectivity")
            }
        } catch (e: Exception) {
            log.error("Error in product generation task: ${e.message}", e)
        }
    }
}
