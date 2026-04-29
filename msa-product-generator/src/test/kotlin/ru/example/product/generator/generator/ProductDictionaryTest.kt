package ru.example.product.generator.generator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import ru.example.product.generator.domain.ProductCategory

/**
 * Unit tests for [ProductDictionary].
 */
class ProductDictionaryTest {

    @ParameterizedTest
    @EnumSource(ProductCategory::class)
    fun `getRandomBrand returns valid brand for each category`(category: ProductCategory) {
        val brand = ProductDictionary.getRandomBrand(category)
        assertNotNull(brand)
        assertFalse(brand.isBlank())
        assertTrue(brand.length in 2..30)
    }

    @ParameterizedTest
    @EnumSource(ProductCategory::class)
    fun `getProductType returns valid product type for each category`(category: ProductCategory) {
        val productType = ProductDictionary.getProductType(category)
        assertNotNull(productType)
        assertFalse(productType.isBlank())
    }

    @Test
    fun `getRandomAdjective returns non-empty adjective`() {
        val adjective = ProductDictionary.getRandomAdjective()
        assertNotNull(adjective)
        assertFalse(adjective.isBlank())
        assertTrue(ProductDictionary.adjectives.contains(adjective))
    }

    @Test
    fun `getRandomColor returns non-empty color`() {
        val color = ProductDictionary.getRandomColor()
        assertNotNull(color)
        assertFalse(color.isBlank())
        assertTrue(ProductDictionary.colors.contains(color))
    }

    @Test
    fun `getRandomMaterial returns non-empty material`() {
        val material = ProductDictionary.getRandomMaterial()
        assertNotNull(material)
        assertFalse(material.isBlank())
        assertTrue(ProductDictionary.materials.contains(material))
    }

    @Test
    fun `getRandomTags returns correct number of tags`() {
        val tags = ProductDictionary.getRandomTags(3)
        assertTrue(tags.size in 1..3)
        tags.forEach { tag ->
            assertTrue(ProductDictionary.tags.contains(tag), "Tag $tag should be in dictionary")
        }
    }

    @Test
    fun `getRandomTags with max 10 returns at most 10 tags`() {
        val tags = ProductDictionary.getRandomTags(10)
        assertTrue(tags.size <= 10)
        assertTrue(tags.size >= 1)
    }

    @Test
    fun `generateDescription returns non-empty description`() {
        val description = ProductDictionary.generateDescription("Headphones", "Plastic", "Black")
        assertNotNull(description)
        assertFalse(description.isBlank())
        assertTrue(description.length > 20)
    }

    @Test
    fun `generateDescription contains product type`() {
        val description = ProductDictionary.generateDescription("Wireless Headphones", "Metal", "Silver")
        assertTrue(description.contains("Wireless Headphones", ignoreCase = true))
    }

    @Test
    fun `getRandomCategorySuffix returns non-empty suffix`() {
        val category = ProductCategory.ELECTRONICS
        val suffix = ProductDictionary.getRandomCategorySuffix(category)
        assertNotNull(suffix)
        assertFalse(suffix.isBlank())
    }

    @Test
    fun `getAllCategories returns all enum values`() {
        val categories = ProductDictionary.getAllCategories()
        assertEquals(ProductCategory.values().size, categories.size)
        for (category in ProductCategory.values()) {
            assertTrue(categories.contains(category))
        }
    }

    @Test
    fun `getBrandsForCategory returns non-empty list for valid category`() {
        val brands = ProductDictionary.getBrandsForCategory(ProductCategory.ELECTRONICS)
        assertTrue(brands.size >= 30, "Electronics should have at least 30 brands, got ${brands.size}")
    }

    @Test
    fun `getProductTypesForCategory returns non-empty list for valid category`() {
        val types = ProductDictionary.getProductTypesForCategory(ProductCategory.ELECTRONICS)
        assertTrue(types.size >= 10, "Electronics should have at least 10 product types, got ${types.size}")
    }

    @Test
    fun `brands dictionary has 30+ items for each category`() {
        for (category in ProductCategory.values()) {
            val brands = ProductDictionary.getBrandsForCategory(category)
            assertTrue(
                brands.size >= 30,
                "Category $category should have at least 30 brands, got ${brands.size}"
            )
        }
    }

    @Test
    fun `product types dictionary has 40+ items for each category`() {
        for (category in ProductCategory.values()) {
            val types = ProductDictionary.getProductTypesForCategory(category)
            assertTrue(
                types.size >= 40,
                "Category $category should have at least 40 product types, got ${types.size}"
            )
        }
    }

    @Test
    fun `adjectives list has 50+ items`() {
        assertTrue(
            ProductDictionary.adjectives.size >= 50,
            "Adjectives should have at least 50 items, got ${ProductDictionary.adjectives.size}"
        )
    }

    @Test
    fun `colors list has 40+ items`() {
        assertTrue(
            ProductDictionary.colors.size >= 40,
            "Colors should have at least 40 items, got ${ProductDictionary.colors.size}"
        )
    }

    @Test
    fun `materials list has 30+ items`() {
        assertTrue(
            ProductDictionary.materials.size >= 30,
            "Materials should have at least 30 items, got ${ProductDictionary.materials.size}"
        )
    }

    @Test
    fun `tags list has 40+ items`() {
        assertTrue(
            ProductDictionary.tags.size >= 40,
            "Tags should have at least 40 items, got ${ProductDictionary.tags.size}"
        )
    }

    @Test
    fun `dictionary methods do not throw exceptions`() {
        assertDoesNotThrow {
            for (i in 1..100) {
                ProductDictionary.getRandomBrand(ProductCategory.ELECTRONICS)
                ProductDictionary.getProductType(ProductCategory.ELECTRONICS)
                ProductDictionary.getRandomAdjective()
                ProductDictionary.getRandomColor()
                ProductDictionary.getRandomMaterial()
                ProductDictionary.getRandomTags(5)
                ProductDictionary.getRandomCategorySuffix(ProductCategory.ELECTRONICS)
            }
        }
    }
}
