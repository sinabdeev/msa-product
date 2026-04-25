package ru.example.product.receiver.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import ru.example.product.receiver.config.converter.TagsReadingConverter
import ru.example.product.receiver.config.converter.TagsWritingConverter
import ru.example.product.receiver.repository.ProductRepository


@Configuration
@EnableJdbcRepositories(basePackageClasses = [ProductRepository::class])
class JdbcConfig(
    private val listToJsonbConverter: TagsWritingConverter,
    private val jsonbToListConverter: TagsReadingConverter
) : AbstractJdbcConfiguration() {

    override fun userConverters(): List<*> {
        return listOf(listToJsonbConverter, jsonbToListConverter)
    }

}
