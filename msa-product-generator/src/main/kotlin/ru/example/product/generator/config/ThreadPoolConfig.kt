package ru.example.product.generator.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.client.RestTemplate
import java.util.concurrent.ThreadPoolExecutor

/**
 * Configuration for thread pool used in parallel product saving.
 *
 * The thread pool is configured via application.yaml properties under `product.batch.thread-pool`.
 *
 * @see ru.example.product.generator.service.ProductServiceImpl.createProductsBatch for usage
 */
@Configuration
@EnableAsync
@EnableScheduling
class ThreadPoolConfig {
    @Bean("productSaveThreadPool")
    fun productSaveThreadPool(
        @Value("\${product.batch.thread-pool.core-size:10}") coreSize: Int,
        @Value("\${product.batch.thread-pool.max-size:50}") maxSize: Int,
        @Value("\${product.batch.thread-pool.queue-capacity:100}") queueCapacity: Int,
        @Value("\${product.batch.thread-pool.keep-alive-seconds:60}") keepAliveSeconds: Int,
        @Value("\${product.batch.thread-pool.thread-name-prefix:product-save-}") threadNamePrefix: String,
        @Value("\${product.batch.thread-pool.allow-core-thread-timeout:true}") allowCoreThreadTimeout: Boolean,
    ): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = coreSize
        executor.maxPoolSize = maxSize
        executor.setQueueCapacity(queueCapacity)
        executor.setKeepAliveSeconds(keepAliveSeconds)
        executor.setThreadNamePrefix(threadNamePrefix)
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeout)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.initialize()
        return executor
    }

    /**
     * Thread pool for product generator async operations.
     *
     * Used by [ru.example.product.generator.scheduler.ProductScheduler] for asynchronous
     * product generation and batch submission.
     *
     * Configuration via application.yaml:
     * ```yaml
     * product:
     *   generator:
     *     thread-pool:
     *       core-size: 2
     *       max-size: 2
     *       queue-capacity: 10
     *       keep-alive-seconds: 60
     *       thread-name-prefix: "product-gen-"
     * ```
     */
    @Bean("productGeneratorThreadPool")
    fun productGeneratorThreadPool(
        @Value("\${product.generator.thread-pool.core-size:2}") coreSize: Int,
        @Value("\${product.generator.thread-pool.max-size:2}") maxSize: Int,
        @Value("\${product.generator.thread-pool.queue-capacity:10}") queueCapacity: Int,
        @Value("\${product.generator.thread-pool.keep-alive-seconds:60}") keepAliveSeconds: Int,
        @Value("\${product.generator.thread-pool.thread-name-prefix:product-gen-}") threadNamePrefix: String,
    ): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = coreSize
        executor.maxPoolSize = maxSize
        executor.setQueueCapacity(queueCapacity)
        executor.setKeepAliveSeconds(keepAliveSeconds)
        executor.setThreadNamePrefix(threadNamePrefix)
        executor.setAllowCoreThreadTimeOut(true)
        executor.setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
        executor.initialize()
        return executor
    }

    /**
     * RestTemplate bean for HTTP communication with the Product API.
     * Used by [ru.example.product.generator.client.ProductBatchClient] for batch product submission.
     */
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
