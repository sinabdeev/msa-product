package ru.example.product.generator.generator

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.example.product.generator.domain.ProductCategory
import ru.example.product.generator.dto.request.CreateProductRequest
import java.math.BigDecimal
import java.util.*

/**
 * Service responsible for generating realistic random product data.
 * Uses ProductDictionary to mix attributes from multiple dictionaries for realistic product generation.
 */
@Service
class ProductGeneratorService {

    private companion object {
        private val log = LoggerFactory.getLogger(ProductGeneratorService::class.java)
    }

    /**
     * Generate a single realistic random product.
     */
    fun generateProduct(): CreateProductRequest {
        val category = ProductDictionary.getAllCategories().random()
        val brand = ProductDictionary.getRandomBrand(category)
        val productType = ProductDictionary.getProductType(category)
        val adjective = ProductDictionary.getRandomAdjective()
        val color = ProductDictionary.getRandomColor()
        val material = ProductDictionary.getRandomMaterial()
        val suffix = ProductDictionary.getRandomCategorySuffix(category)
        val tags = ProductDictionary.getRandomTags(5)

        val sku = generateSku(brand, productType, color)
        val name = generateName(adjective, productType, color, brand, suffix)
        val description = ProductDictionary.generateDescription(productType, material, color)
        val price = generatePrice(category)
        val quantity = (0..100).random()
        val weight = generateWeight(productType)
        val isAvailable = quantity > 0

        log.debug("Generated product: sku=$sku, name=$name, price=$price, category=$category")

        return CreateProductRequest(
            sku = sku,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            weight = weight,
            isAvailable = isAvailable,
            category = category.name,
            tags = tags,
        )
    }

    /**
     * Generate a list of realistic random products.
     */
    fun generateProducts(count: Int): List<CreateProductRequest> {
        require(count > 0) { "Product count must be positive, got: $count" }
        require(count <= 100) { "Product count cannot exceed 100, got: $count" }

        log.info("Generating $count random products")
        val products = (1..count).map { generateProduct() }
        log.info("Successfully generated ${products.size} products")

        return products
    }

    /**
     * Generate a unique SKU based on brand, product type, and color.
     * Format: {BRAND_PREFIX}-{PRODUCT_TYPE}-{COLOR}-{RANDOM}
     */
    private fun generateSku(brand: String, productType: String, color: String): String {
        val brandPrefix = brand.replace(Regex("[^A-Z]"), "").take(3).uppercase()
        val typeCode = productType.split(" ").firstOrNull()?.take(3)?.uppercase() ?: "PRD"
        val colorCode = color.split(" ").firstOrNull()?.take(3)?.uppercase() ?: "BLK"
        val randomPart = (1000..9999).random()
        return "$brandPrefix-$typeCode-$colorCode-$randomPart"
    }

    /**
     * Generate a realistic product name.
     * Format: {Adjective} {ProductType} {Color} {Brand} {Suffix}
     */
    private fun generateName(
        adjective: String,
        productType: String,
        color: String,
        brand: String,
        suffix: String,
    ): String {
        return "$adjective $productType $color $brand $suffix"
    }

    /**
     * Generate a realistic price based on product category.
     * Different categories have different price ranges.
     */
    private fun generatePrice(category: ProductCategory): BigDecimal {
        val priceRange = when (category) {
            ProductCategory.ELECTRONICS -> 2999..299990
            ProductCategory.CLOTHING -> 1990..49990
            ProductCategory.FOOD -> 290..9990
            ProductCategory.HOME_GOODS -> 490..79990
            ProductCategory.APPLIANCES -> 1990..199990
            ProductCategory.SMART_HOME -> 990..59990
        }
        val priceCents = priceRange.random()
        return BigDecimal(priceCents).divide(BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP)
    }

    /**
     * Generate a realistic weight based on product type.
     * Returns weight in kilograms with 2 decimal places.
     */
    private fun generateWeight(productType: String): Double {
        val weightRange: ClosedRange<Double> = when {
            productType.contains("Headphone", ignoreCase = true) ||
                productType.contains("Earbud", ignoreCase = true) ||
                productType.contains("Speaker", ignoreCase = true) -> 0.10..1.50

            productType.contains("Watch", ignoreCase = true) ||
                productType.contains("Tracker", ignoreCase = true) -> 0.02..0.30

            productType.contains("Laptop", ignoreCase = true) ||
                productType.contains("Monitor", ignoreCase = true) -> 1.0..5.0

            productType.contains("Camera", ignoreCase = true) ||
                productType.contains("Drone", ignoreCase = true) -> 0.20..3.0

            productType.contains("Shoe", ignoreCase = true) ||
                productType.contains("Sneaker", ignoreCase = true) ||
                productType.contains("Boot", ignoreCase = true) ||
                productType.contains("Sandal", ignoreCase = true) -> 0.30..1.50

            productType.contains("Vacuum", ignoreCase = true) ||
                productType.contains("Purifier", ignoreCase = true) ||
                productType.contains("Humidifier", ignoreCase = true) -> 1.0..8.0

            productType.contains("Blender", ignoreCase = true) ||
                productType.contains("Mixer", ignoreCase = true) ||
                productType.contains("Coffee", ignoreCase = true) -> 0.50..5.0

            productType.contains("Bag", ignoreCase = true) ||
                productType.contains("Backpack", ignoreCase = true) -> 0.30..2.0

            productType.contains("Rug", ignoreCase = true) ||
                productType.contains("Mirror", ignoreCase = true) -> 2.0..15.0

            else -> 0.10..10.0
        }
        val min = weightRange.start
        val max = weightRange.endInclusive
        val randomWeight = min + Math.random() * (max - min)
        return String.format(Locale.US, "%.2f", randomWeight).toDouble()
    }
}
