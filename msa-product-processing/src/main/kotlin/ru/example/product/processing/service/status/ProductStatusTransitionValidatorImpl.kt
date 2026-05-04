package ru.example.product.processing.service.status

import org.springframework.stereotype.Component
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.domain.status.ProductStatusTransitions

/**
 * Реализация валидатора переходов статусов продукта на основе матрицы переходов.
 */
@Component
class ProductStatusTransitionValidatorImpl : ProductStatusTransitionValidator {

    override fun isTransitionAllowed(current: ProductStatus, target: ProductStatus): Boolean {
        // Переход в тот же статус считается допустимым (но может быть бесполезным)
        if (current == target) {
            return true
        }
        val allowedTargets = ProductStatusTransitions.TRANSITION_MAP[current]
            ?: return false // если текущий статус отсутствует в карте, переход запрещен
        return target in allowedTargets
    }

    override fun getAllowedTransitions(current: ProductStatus): Set<ProductStatus> {
        return ProductStatusTransitions.TRANSITION_MAP[current] ?: emptySet()
    }
}