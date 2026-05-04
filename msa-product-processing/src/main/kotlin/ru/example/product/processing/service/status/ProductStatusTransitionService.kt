package ru.example.product.processing.service.status

import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

/**
 * Сервис для выполнения перехода статуса продукта.
 */
interface ProductStatusTransitionService {

    /**
     * Выполняет переход статуса продукта по его идентификатору.
     * @param productId идентификатор продукта
     * @param targetStatus целевой статус
     * @param reason причина перехода (опционально)
     * @param userId идентификатор пользователя, инициировавшего переход (опционально)
     * @return обновленная сущность продукта
     * @throws ru.example.product.processing.exception.ProductNotFoundException если продукт не найден
     * @throws ru.example.product.processing.exception.InvalidStatusTransitionException если переход недопустим
     */
    fun transitionProduct(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String? = null,
        userId: UUID? = null
    ): ProductEntity

    /**
     * Выполняет переход статуса продукта для переданной сущности.
     * @param product сущность продукта
     * @param targetStatus целевой статус
     * @param reason причина перехода (опционально)
     * @param userId идентификатор пользователя, инициировавшего переход (опционально)
     * @return обновленная сущность продукта
     * @throws ru.example.product.processing.exception.InvalidStatusTransitionException если переход недопустим
     */
    fun transitionProduct(
        product: ProductEntity,
        targetStatus: ProductStatus,
        reason: String? = null,
        userId: UUID? = null
    ): ProductEntity

    /**
     * Возвращает допустимые переходы из текущего статуса.
     * @param current текущий статус
     * @return множество допустимых целевых статусов
     */
    fun getAllowedTransitions(current: ProductStatus): Set<ProductStatus>
}