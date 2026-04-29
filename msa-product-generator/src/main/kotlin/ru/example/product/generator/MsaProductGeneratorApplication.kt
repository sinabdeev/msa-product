package ru.example.product.generator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync // Добавьте импорт

@EnableAsync // Активирует поддержку асинхронности
@SpringBootApplication
class MsaProductGeneratorApplication

fun main(args: Array<String>) {
    runApplication<MsaProductGeneratorApplication>(*args)
}
