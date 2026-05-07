package ru.example.product.processing.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * Service for scheduled tasks.
 * Responsible only for regular task execution (scheduling).
 */
@Service
class ScheduleService(
    private val productService: ProductService,
) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduleService::class.java)

    /**
     * Process all products periodically.
     */
    @Scheduled(
        fixedDelayString = "\${product.scheduling.product-processing.interval:60000}",
        initialDelayString = "\${product.scheduling.product-processing.initial-delay:10000}",
    )
    fun processAllProducts() {
        logger.info("Scheduled task: starting product processing")
        productService.processProducts()
        logger.info("Scheduled task: product processing completed")
    }
}
