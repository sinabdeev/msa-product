package ru.example.product.generator.generator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import ru.example.product.generator.domain.ProductCategory
import java.math.BigDecimal

/**
 * Unit tests for [ProductGeneratorService].
 */
class ProductGeneratorServiceTest {

    private val generator = ProductGeneratorService()

    @Test
    fun `generateProduct returns valid CreateProductRequest`() {
        val product = generator.generateProduct()

        assertNotNull(product.sku)
        assertFalse(product.sku.isBlank())
        assertNotNull(product.name)
        assertFalse(product.name.isBlank())
        assertNotNull(product.description)
        assertFalse(product.description.isBlank())
        assertNotNull(product.price)
        assertTrue(product.price > BigDecimal.ZERO)
        assertNotNull(product.quantity)
        assertTrue(product.quantity >= 0)
        assertNotNull(product.category)
        assertFalse(product.category.isBlank())
    }

    @Test
    fun `generateProduct SKU contains brand prefix`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            assertTrue(
                product.sku.matches(Regex("[A-Z]{1,3}-[A-Z]+-.*")),
                "SKU '$product.sku' should start with 1-3 uppercase letters"
            )
        }
    }

    @Test
    fun `generateProduct name is non-empty and reasonable length`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            assertFalse(product.name.isBlank())
            assertTrue(
                product.name.length in 10..200,
                "Name length ${product.name.length} should be between 10 and 200"
            )
        }
    }

    @Test
    fun `generateProduct description is non-empty`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            assertFalse(product.description.isBlank())
            assertTrue(product.description.length > 20)
        }
    }

    @Test
    fun `generateProduct price is positive`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            assertTrue(
                product.price > BigDecimal.ZERO,
                "Price ${product.price} should be positive"
            )
        }
    }

    @Test
    fun `generateProduct quantity is non-negative`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            assertTrue(
                product.quantity >= 0,
                "Quantity ${product.quantity} should be non-negative"
            )
        }
    }

    @Test
    fun `generateProduct isAvailable matches quantity`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            if (product.quantity > 0) {
                assertTrue(product.isAvailable, "isAvailable should be true when quantity > 0")
            }
        }
    }

    @Test
    fun `generateProduct category is valid enum name`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            val validCategories = ProductCategory.values().map { it.name }
            assertTrue(
                validCategories.contains(product.category),
                "Category '${product.category}' should be valid. Valid: $validCategories"
            )
        }
    }

    @Test
    fun `generateProduct tags are within limits`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            assertTrue(
                product.tags.size in 0..5,
                "Tags count ${product.tags.size} should be between 0 and 5"
            )
        }
    }

    @Test
    fun `generateProduct weight is positive when not null`() {
        for (i in 1..50) {
            val product = generator.generateProduct()
            val weight = product.weight
            if (weight != null) {
                assertTrue(
                    weight > 0,
                    "Weight $weight should be positive"
                )
            }
        }
    }

    @Test
    fun `generateProducts with count 10 returns 10 products`() {
        val products = generator.generateProducts(10)
        assertEquals(10, products.size)
    }

    @Test
    fun `generateProducts with count 1 returns 1 product`() {
        val products = generator.generateProducts(1)
        assertEquals(1, products.size)
        assertNotNull(products.first())
    }

    @Test
    fun `generateProducts with count 50 returns 50 products`() {
        val products = generator.generateProducts(50)
        assertEquals(50, products.size)
    }

    @Test
    fun `generateProducts all products have valid data`() {
        val products = generator.generateProducts(20)
        for (product in products) {
            assertNotNull(product.sku)
            assertFalse(product.sku.isBlank())
            assertNotNull(product.name)
            assertFalse(product.name.isBlank())
            assertNotNull(product.price)
            assertTrue(product.price > BigDecimal.ZERO)
            assertNotNull(product.quantity)
            assertTrue(product.quantity >= 0)
        }
    }

    @Test
    fun `generateProducts with count 0 throws exception`() {
        val exception = assertThrows<IllegalArgumentException> {
            generator.generateProducts(0)
        }
        assertNotNull(exception)
    }

    @Test
    fun `generateProducts with negative count throws exception`() {
        val exception = assertThrows<IllegalArgumentException> {
            generator.generateProducts(-5)
        }
        assertNotNull(exception)
    }

    @Test
    fun `generateProducts with count 101 throws exception`() {
        val exception = assertThrows<IllegalArgumentException> {
            generator.generateProducts(101)
        }
        assertNotNull(exception)
    }

    @Test
    fun `generateProducts does not throw for valid counts`() {
        assertDoesNotThrow {
            for (count in 1..100) {
                generator.generateProducts(count)
            }
        }
    }

    @Test
    fun `generated products have variety of categories`() {
        val products = generator.generateProducts(100)
        val categories = products.map { it.category }.toSet()
        assertTrue(
            categories.size > 1,
            "Should have variety of categories, got only: $categories"
        )
    }

    @Test
    fun `generated products have unique SKUs`() {
        val products = generator.generateProducts(50)
        val uniqueSkus = products.map { it.sku }.toSet()
        assertEquals(
            products.size,
            uniqueSkus.size,
            "All SKUs should be unique"
        )
    }
}
