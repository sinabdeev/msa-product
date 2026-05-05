package ru.example.product.processing.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import ru.example.product.processing.config.converter.ProductStatusReadingConverter
import ru.example.product.processing.config.converter.ProductStatusWritingConverter
import ru.example.product.processing.config.converter.TagsReadingConverter
import ru.example.product.processing.config.converter.TagsWritingConverter
import ru.example.product.processing.repository.ProductRepository

@Configuration
@EnableJdbcRepositories(basePackageClasses = [ProductRepository::class])
class JdbcConfig(
    private val listToJsonbConverter: TagsWritingConverter,
    private val jsonbToListConverter: TagsReadingConverter,
    private val productStatusWritingConverter: ProductStatusWritingConverter,
    private val productStatusReadingConverter: ProductStatusReadingConverter,
) : AbstractJdbcConfiguration() {
    override fun userConverters(): List<*> {
        return listOf(
            listToJsonbConverter,
            jsonbToListConverter,
            productStatusWritingConverter,
            productStatusReadingConverter,
        )
    }
}
