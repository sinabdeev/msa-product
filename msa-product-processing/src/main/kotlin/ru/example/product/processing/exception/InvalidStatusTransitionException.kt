package ru.example.product.processing.exception

import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

/**
 * Исключение, выбрасываемое при попытке недопустимого перехода статуса продукта.
 * @property productId идентификатор продукта (может быть null, если продукт неизвестен)
 * @property from исходный статус
 * @property to целевой статус
 */
class InvalidStatusTransitionException(
    val productId: UUID? = null,
    val from: ProductStatus,
    val to: ProductStatus,
    message: String =
        "Transition from $from to $to is not allowed" +
            (productId?.let { " for product $it" } ?: ""),
) : RuntimeException(message)
