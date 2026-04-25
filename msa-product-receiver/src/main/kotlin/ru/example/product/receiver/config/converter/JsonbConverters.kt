package ru.example.product.receiver.config.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.stereotype.Component
import ru.example.product.receiver.domain.TagsWrapper

@WritingConverter
@Component
class TagsWritingConverter(private val objectMapper: ObjectMapper) : Converter<TagsWrapper, PGobject> {
    override fun convert(source: TagsWrapper): PGobject = PGobject().apply {
        type = "jsonb"
        value = objectMapper.writeValueAsString(source.values)
    }
}

@ReadingConverter
@Component
class TagsReadingConverter(private val objectMapper: ObjectMapper) : Converter<PGobject, TagsWrapper> {
    override fun convert(source: PGobject): TagsWrapper {
        val list = source.value?.let { objectMapper.readValue<List<String>>(it) } ?: emptyList()
        return TagsWrapper(list)
    }
}
