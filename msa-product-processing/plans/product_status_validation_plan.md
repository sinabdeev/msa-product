# План реализации валидации переходов на основе матрицы

## Цель
Реализовать компонент `ProductStatusTransitionValidator`, который проверяет допустимость переходов между статусами продукта на основе предопределенной матрицы.

## Шаги реализации

### 1. Создать объект матрицы переходов
- Создать файл `src/main/kotlin/ru/example/product/processing/domain/status/ProductStatusTransitions.kt`
- Определить объект `ProductStatusTransitions` с константой `TRANSITION_MAP` типа `Map<ProductStatus, Set<ProductStatus>>`
- Заполнить карту согласно правилам из `product_status_workflow.md` (матрица допустимых переходов)

### 2. Создать интерфейс валидатора
- Создать файл `src/main/kotlin/ru/example/product/processing/service/status/ProductStatusTransitionValidator.kt`
- Определить интерфейс с методами `isTransitionAllowed` и `getAllowedTransitions`

### 3. Реализовать валидатор
- Создать файл `src/main/kotlin/ru/example/product/processing/service/status/ProductStatusTransitionValidatorImpl.kt`
- Реализовать класс `ProductStatusTransitionValidatorImpl`, имплементирующий интерфейс
- Использовать `ProductStatusTransitions.TRANSITION_MAP` для проверок
- Обработать edge cases: null-статусы, одинаковые статусы (переход в себя), отсутствие записи в карте

### 4. Создать кастомное исключение
- Создать файл `src/main/kotlin/ru/example/product/processing/exception/InvalidStatusTransitionException.kt`
- Определить класс исключения с полями `productId`, `from`, `to` и понятным сообщением

### 5. Написать unit-тесты для валидатора
- Создать файл `src/test/kotlin/ru/example/product/processing/service/status/ProductStatusTransitionValidatorTest.kt`
- Протестировать все допустимые переходы (позитивные тесты)
- Протестировать недопустимые переходы (негативные тесты)
- Протестировать пограничные случаи (null, одинаковые статусы)

### 6. Интегрировать валидатор в Spring контекст
- Добавить аннотацию `@Component` к `ProductStatusTransitionValidatorImpl`
- Убедиться, что бин доступен для инъекции

## Детали реализации

### Класс ProductStatusTransitions.kt
```kotlin
package ru.example.product.processing.domain.status

import ru.example.product.processing.domain.ProductStatus

object ProductStatusTransitions {
    val TRANSITION_MAP: Map<ProductStatus, Set<ProductStatus>> = mapOf(
        ProductStatus.DRAFT to setOf(
            ProductStatus.PENDING_REVIEW,
            ProductStatus.ARCHIVED
        ),
        ProductStatus.PENDING_REVIEW to setOf(
            ProductStatus.REVIEWED,
            ProductStatus.ARCHIVED,
            ProductStatus.DRAFT
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
            ProductStatus.ACTIVE
        )
    )
}
```

### Класс ProductStatusTransitionValidatorImpl.kt
```kotlin
package ru.example.product.processing.service.status

import org.springframework.stereotype.Component
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.domain.status.ProductStatusTransitions

@Component
class ProductStatusTransitionValidatorImpl : ProductStatusTransitionValidator {

    override fun isTransitionAllowed(current: ProductStatus, target: ProductStatus): Boolean {
        if (current == target) {
            // Переход в тот же статус считается допустимым (но может быть бесполезным)
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
```

### Класс InvalidStatusTransitionException.kt
```kotlin
package ru.example.product.processing.exception

import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

class InvalidStatusTransitionException(
    val productId: UUID? = null,
    val from: ProductStatus,
    val to: ProductStatus,
    message: String = "Transition from $from to $to is not allowed for product ${productId ?: "unknown"}"
) : RuntimeException(message)
```

## Тестовые сценарии

### Позитивные тесты
- DRAFT → PENDING_REVIEW (разрешен)
- PENDING_REVIEW → REVIEWED (разрешен)
- REVIEWED → APPROVED (разрешен)
- REVIEWED → REJECTED (разрешен)
- REJECTED → PENDING_REVIEW (разрешен)
- APPROVED → ACTIVE (разрешен)
- ACTIVE → PROCESSED (разрешен)
- PROCESSED → SHIPPED (разрешен)
- SHIPPED → ARCHIVED (разрешен)
- ARCHIVED → ACTIVE (разрешен, административное восстановление)

### Негативные тесты
- DRAFT → REVIEWED (запрещен)
- PENDING_REVIEW → SHIPPED (запрещен)
- APPROVED → REJECTED (запрещен)
- ACTIVE → DRAFT (запрещен)
- SHIPPED → PROCESSED (запрещен)

### Edge cases
- Тот же статус (current == target) → разрешен
- Статус, отсутствующий в карте (если добавить новый статус позже) → запрещен
- Null значения (не должны передаваться, т.к. enum не nullable)

## Интеграция с другими компонентами

Валидатор будет использоваться:
1. В `ProductStatusTransitionService` перед выполнением перехода
2. В `ProductService.getPossibleTransitions` для возврата допустимых целевых статусов
3. В REST контроллерах (если есть) для предварительной валидации запросов

## Следующие шаги после реализации валидатора

1. Реализовать `ProductStatusHistoryService` для записи истории переходов
2. Реализовать `ProductStatusTransitionService` с транзакционной логикой
3. Расширить `ProductService` новыми методами
4. Написать интеграционные тесты для полных путей

## Временная оценка
- Создание файлов и написание кода: 2-3 часа
- Написание unit-тестов: 1-2 часа
- Отладка и интеграция: 1 час

**Итого**: 4-6 часов на реализацию валидации переходов.