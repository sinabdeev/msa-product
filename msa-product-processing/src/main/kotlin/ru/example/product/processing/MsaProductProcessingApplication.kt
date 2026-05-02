package ru.example.product.processing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync // Добавьте импорт

@EnableAsync // Активирует поддержку асинхронности
@SpringBootApplication
class MsaProductReceiverApplication

fun main(args: Array<String>) {
    runApplication<MsaProductReceiverApplication>(*args)
}
