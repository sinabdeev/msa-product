package ru.example.product.processing.config.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import ru.example.product.processing.domain.ProductStatus

/**
 * Converts ProductStatus enum to String for database storage.
 * Uses the enum name (e.g., "DRAFT", "ACTIVE") to match the VARCHAR column type.
 */
@WritingConverter
class ProductStatusWritingConverter : Converter<ProductStatus, String> {
    override fun convert(source: ProductStatus): String = source.name
}
