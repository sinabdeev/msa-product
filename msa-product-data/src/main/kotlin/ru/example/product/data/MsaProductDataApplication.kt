package ru.example.product.data

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync // Добавьте импорт

@EnableAsync // Активирует поддержку асинхронности
@SpringBootApplication
class MsaProductDataApplication

fun main(args: Array<String>) {
    runApplication<MsaProductDataApplication>(*args)
}
