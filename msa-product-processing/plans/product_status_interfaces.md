# Интерфейсы и методы для системы управления статусами продукта

## 1. ProductStatusTransitionValidator

```kotlin
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
    fun isTransitionAllowed(current: ProductStatus, target: ProductStatus): Boolean

    /**
     * Возвращает множество статусов, в которые можно перейти из текущего.
     * @param current текущий статус
     * @return множество допустимых целевых статусов
     */
    fun getAllowedTransitions(current: ProductStatus): Set<ProductStatus>
}
```

## 2. ProductStatusTransitionService

```kotlin
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
     * @return обновленная сущность продукта
     * @throws ProductNotFoundException если продукт не найден
     * @throws InvalidStatusTransitionException если переход недопустим
     */
    fun transitionProduct(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String? = null
    ): ProductEntity

    /**
     * Выполняет переход статуса продукта для переданной сущности.
     * @param product сущность продукта
     * @param targetStatus целевой статус
     * @param reason причина перехода (опционально)
     * @return обновленная сущность продукта
     * @throws InvalidStatusTransitionException если переход недопустим
     */
    fun transitionProduct(
        product: ProductEntity,
        targetStatus: ProductStatus,
        reason: String? = null
    ): ProductEntity
}
```

## 3. ProductStatusHistoryService

```kotlin
package ru.example.product.processing.service.status.history

import ru.example.product.processing.domain.ProductStatus
import java.time.Instant
import java.util.UUID

/**
 * Сервис для ведения истории изменений статуса продукта.
 */
interface ProductStatusHistoryService {

    /**
     * Записывает факт перехода статуса продукта.
     * @param productId идентификатор продукта
     * @param from исходный статус
     * @param to целевой статус
     * @param reason причина перехода (опционально)
     */
    fun recordTransition(
        productId: UUID,
        from: ProductStatus,
        to: ProductStatus,
        reason: String? = null
    )

    /**
     * Возвращает историю переходов статуса для указанного продукта.
     * @param productId идентификатор продукта
     * @return список записей истории в хронологическом порядке (от старых к новым)
     */
    fun getHistory(productId: UUID): List<ProductStatusHistoryRecord>
}

/**
 * Запись об изменении статуса продукта.
 */
data class ProductStatusHistoryRecord(
    val id: UUID,
    val productId: UUID,
    val fromStatus: ProductStatus,
    val toStatus: ProductStatus,
    val timestamp: Instant,
    val userId: UUID?, // идентификатор пользователя, инициировавшего переход (опционально)
    val reason: String? = null
)
```

## 4. ProductStatusEventPublisher

```kotlin
package ru.example.product.processing.service.status.events

import ru.example.product.processing.domain.ProductStatus
import java.time.Instant
import java.util.UUID

/**
 * Издатель событий изменения статуса продукта.
 */
interface ProductStatusEventPublisher {

    /**
     * Публикует событие изменения статуса продукта.
     * @param event событие изменения статуса
     */
    fun publishStatusChanged(event: ProductStatusChangedEvent)
}

/**
 * Событие изменения статуса продукта.
 */
data class ProductStatusChangedEvent(
    val eventId: UUID = UUID.randomUUID(),
    val productId: UUID,
    val oldStatus: ProductStatus,
    val newStatus: ProductStatus,
    val timestamp: Instant = Instant.now(),
    val metadata: Map<String, Any> = emptyMap()
)
```

## 5. Расширение ProductService

```kotlin
package ru.example.product.processing.service

import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

/**
 * Сервис для операций с продуктами (расширенный).
 */
interface ProductService {

    // Существующие методы
    fun deleteProduct(id: UUID)
    fun existsBySku(sku: String): Boolean

    // Новые методы для управления статусами

    /**
     * Обновляет статус продукта.
     * @param productId идентификатор продукта
     * @param targetStatus целевой статус
     * @param reason причина изменения (опционально)
     * @return обновленная сущность продукта
     */
    fun updateStatus(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String? = null
    ): ProductEntity

    /**
     * Возвращает возможные переходы статуса для указанного продукта.
     * @param productId идентификатор продукта
     * @return множество статусов, в которые можно перейти из текущего статуса продукта
     */
    fun getPossibleTransitions(productId: UUID): Set<ProductStatus>
}
```

## 6. Исключения

```kotlin
package ru.example.product.processing.exception

import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

/**
 * Исключение, выбрасываемое при попытке недопустимого перехода статуса.
 */
class InvalidStatusTransitionException(
    val productId: UUID? = null,
    val from: ProductStatus,
    val to: ProductStatus,
    message: String = "Transition from $from to $to is not allowed"
) : RuntimeException(message)

/**
 * Исключение, выбрасываемое когда продукт не найден.
 * (Уже существует ProductNotFoundException)
 */
```

## 7. Конфигурация матрицы переходов

```kotlin
package ru.example.product.processing.domain.status

import ru.example.product.processing.domain.ProductStatus

/**
 * Объект, содержащий матрицу допустимых переходов между статусами продукта.
 */
object ProductStatusTransitions {

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
}
```

## 8. Планируемая структура пакетов

```
src/main/kotlin/ru/example/product/processing/
├── domain/
│   ├── ProductStatus.kt
│   ├── ProductEntity.kt
│   ├── ProductCategory.kt
│   └── status/
│       ├── ProductStatusTransitions.kt
│       └── ProductStatusHistoryRecord.kt
├── service/
│   ├── ProductService.kt (расширенный)
│   ├── ProductServiceImpl.kt
│   └── status/
│       ├── ProductStatusTransitionService.kt
│       ├── ProductStatusTransitionServiceImpl.kt
│       ├── ProductStatusTransitionValidator.kt
│       ├── ProductStatusTransitionValidatorImpl.kt
│       ├── history/
│       │   ├── ProductStatusHistoryService.kt
│       │   └── ProductStatusHistoryServiceImpl.kt
│       └── events/
│           ├── ProductStatusEventPublisher.kt
│           └── ProductStatusEventPublisherImpl.kt
├── repository/
│   ├── ProductRepository.kt
│   └── ProductStatusHistoryRepository.kt
└── exception/
    ├── ProductNotFoundException.kt
    └── InvalidStatusTransitionException.kt
```

## Следующие шаги

1. Создать перечисленные интерфейсы и классы в соответствующих пакетах.
2. Реализовать валидатор на основе матрицы переходов.
3. Реализовать сервис истории статусов с использованием Spring Data JDBC.
4. Реализовать сервис переходов с транзакционностью.
5. Расширить ProductServiceImpl, добавив методы updateStatus и getPossibleTransitions.
6. Написать unit- и интеграционные тесты.