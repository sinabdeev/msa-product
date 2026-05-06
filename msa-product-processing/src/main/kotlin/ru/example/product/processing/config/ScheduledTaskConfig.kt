package ru.example.product.processing.config

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Конфигурация запланированных задач.
 * Задачи выполняются по фиксированному интервалу (fixedRate).
 */
@Component
class ScheduledTaskConfig {
    private companion object {
        val log = LoggerFactory.getLogger(ScheduledTaskConfig::class.java)
    }

    /**
     * Пример запланированной задачи, выполняемой каждые N миллисекунд.
     * Интервал настраивается через application.yaml:
     *   product.scheduling.do-something.interval=60000
     */
    @Scheduled(
        fixedRateString = "\${product.scheduling.do-something.interval:60000}")
    fun doSomething() {
        log.debug("Starting scheduled task 'doSomething'...")
        try {
            // TODO: implement task logic here
            log.debug("Scheduled task 'doSomething' completed successfully")
        } catch (e: Exception) {
            log.error("Error during scheduled task 'doSomething'", e)
        }
    }
}
