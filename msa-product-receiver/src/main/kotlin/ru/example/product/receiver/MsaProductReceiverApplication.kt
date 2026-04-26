package ru.example.product.receiver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MsaProductReceiverApplication

fun main(args: Array<String>) {
    runApplication<MsaProductReceiverApplication>(*args)
}
