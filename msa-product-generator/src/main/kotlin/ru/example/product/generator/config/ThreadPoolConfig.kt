package ru.example.product.generator.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
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
}
