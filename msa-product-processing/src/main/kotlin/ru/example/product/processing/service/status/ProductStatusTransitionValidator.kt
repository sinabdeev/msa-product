package ru.example.product.processing.service.status

import ru.example.product.processing.domain.ProductStatus

/**
 * Валидатор допустимости переходов между статусами продукта.
 */
interface ProductStatusTransitionValidator {
    /**
     * Проверяет, разрешен ли переход из текущего статуса в целевой.
     * @param current текущий статус продукта
     * @param target целевой статус
     * @return true если переход разрешен, false в противном случае
     */
    fun isTransitionAllowed(
        current: ProductStatus,
        target: ProductStatus,
    ): Boolean

    /**
     * Возвращает множество статусов, в которые можно перейти из текущего.
     * @param current текущий статус
     * @return множество допустимых целевых статусов
     */
    fun getAllowedTransitions(current: ProductStatus): Set<ProductStatus>
}
