package ru.example.product.processing.domain.status

import ru.example.product.processing.domain.ProductStatus

/**
 * Объект, содержащий матрицу допустимых переходов между статусами продукта.
 * Основан на бизнес-правилах, описанных в документации.
 */
object ProductStatusTransitions {

    /**
     * Карта, где ключ — текущий статус, значение — множество статусов, в которые можно перейти.
     * Переход в тот же статус считается допустимым (но обычно бесполезным).
     */
    val TRANSITION_MAP: Map<ProductStatus, Set<ProductStatus>> = mapOf(
        ProductStatus.DRAFT to setOf(
            ProductStatus.PENDING_REVIEW,
            ProductStatus.ARCHIVED
        ),
        ProductStatus.PENDING_REVIEW to setOf(
            ProductStatus.REVIEWED,
            ProductStatus.ARCHIVED,
            ProductStatus.DRAFT // опционально, если разрешен возврат в черновик
        ),
        ProductStatus.REVIEWED to setOf(
            ProductStatus.APPROVED,
            ProductStatus.REJECTED
        ),
        ProductStatus.APPROVED to setOf(
            ProductStatus.ACTIVE,
            ProductStatus.ARCHIVED
        ),
        ProductStatus.REJECTED to setOf(
            ProductStatus.PENDING_REVIEW,
            ProductStatus.ARCHIVED
        ),
        ProductStatus.ACTIVE to setOf(
            ProductStatus.PROCESSED,
            ProductStatus.ARCHIVED
        ),
        ProductStatus.PROCESSED to setOf(
            ProductStatus.SHIPPED,
            ProductStatus.ARCHIVED
        ),
        ProductStatus.SHIPPED to setOf(
            ProductStatus.ARCHIVED
        ),
        ProductStatus.ARCHIVED to setOf(
            ProductStatus.ACTIVE // только через административное восстановление
        )
    )

    /**
     * Проверяет, разрешен ли переход из текущего статуса в целевой.
     * @param current текущий статус
     * @param target целевой статус
     * @return true если переход разрешен, false в противном случае
     */
    fun isTransitionAllowed(current: ProductStatus, target: ProductStatus): Boolean {
        if (current == target) return true
        val allowedTargets = TRANSITION_MAP[current] ?: return false
        return target in allowedTargets
    }

    /**
     * Возвращает множество статусов, в которые можно перейти из текущего.
     * @param current текущий статус
     * @return множество допустимых целевых статусов (может быть пустым)
     */
    fun getAllowedTransitions(current: ProductStatus): Set<ProductStatus> {
        return TRANSITION_MAP[current] ?: emptySet()
    }
}