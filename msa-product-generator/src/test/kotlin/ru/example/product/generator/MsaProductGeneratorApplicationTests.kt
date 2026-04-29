package ru.example.product.generator

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Database configuration issues in test environment")
class MsaProductgeneratorApplicationTests {
    @Test
    fun contextLoads() {
    }
}
