package ru.example.product.processing.config.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.stereotype.Component
import ru.example.product.processing.domain.ProductStatus

/**
 * Converts String from database to ProductStatus enum.
 * Uses the companion object's fromName method for case-insensitive matching.
 */
@ReadingConverter
@Component
class ProductStatusReadingConverter : Converter<String, ProductStatus> {
    override fun convert(source: String): ProductStatus =
        ProductStatus.fromName(source) ?: throw IllegalArgumentException(
            "Invalid ProductStatus value: '$source'. Valid values: ${ProductStatus.values().joinToString(
                ", ",
            ) { it.name }}",
        )
}
