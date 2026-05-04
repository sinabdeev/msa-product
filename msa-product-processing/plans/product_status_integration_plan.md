# План интеграции системы управления статусами с существующим ProductService

## Текущее состояние
- `ProductService` — интерфейс с двумя методами: `deleteProduct` и `existsBySku`
- `ProductServiceImpl` — реализация, использующая `ProductRepository`
- `ProductEntity` содержит поле `status: ProductStatus?`
- `ProductRepository` расширяет `CrudRepository`

## Цель интеграции
Расширить `ProductService` новыми методами для управления статусами продукта, делегируя логику переходов в `ProductStatusTransitionService`.

## Шаги интеграции

### 1. Расширение интерфейса ProductService
Добавить в `src/main/kotlin/ru/example/product/processing/service/ProductService.kt` два новых метода:

```kotlin
package ru.example.product.processing.service

import ru.example.product.processing.domain.ProductEntity
import ru.example.product.processing.domain.ProductStatus
import java.util.UUID

interface ProductService {
    // существующие методы
    fun deleteProduct(id: UUID)
    fun existsBySku(sku: String): Boolean

    // новые методы
    fun updateStatus(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String? = null
    ): ProductEntity

    fun getPossibleTransitions(productId: UUID): Set<ProductStatus>
}
```

### 2. Обновление ProductServiceImpl
Добавить в `src/main/kotlin/ru/example/product/processing/service/ProductServiceImpl.kt`:
- Зависимость от `ProductStatusTransitionService`
- Реализацию новых методов

```kotlin
package ru.example.product.processing.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.exception.ProductNotFoundException
import ru.example.product.processing.repository.ProductRepository
import ru.example.product.processing.service.status.ProductStatusTransitionService
import java.util.*

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val statusTransitionService: ProductStatusTransitionService
) : ProductService {
    private val logger: Logger = LoggerFactory.getLogger(ProductServiceImpl::class.java)

    @Transactional
    override fun deleteProduct(id: UUID) {
        // существующая реализация
    }

    @Transactional(readOnly = true)
    override fun existsBySku(sku: String): Boolean {
        // существующая реализация
    }

    @Transactional
    override fun updateStatus(
        productId: UUID,
        targetStatus: ProductStatus,
        reason: String?
    ): ProductEntity {
        logger.info("Updating status for product {} to {} with reason: {}", productId, targetStatus, reason)
        return statusTransitionService.transitionProduct(productId, targetStatus, reason)
    }

    @Transactional(readOnly = true)
    override fun getPossibleTransitions(productId: UUID): Set<ProductStatus> {
        logger.debug("Getting possible transitions for product {}", productId)
        val product = productRepository.findById(productId)
            .orElseThrow { ProductNotFoundException(productId) }
        val currentStatus = product.status ?: ProductStatus.DRAFT
        return statusTransitionService.getAllowedTransitions(currentStatus)
    }
}
```

### 3. Создание ProductStatusTransitionService
Реализовать сервис `ProductStatusTransitionServiceImpl` в пакете `ru.example.product.processing.service.status`.

Этот сервис будет зависеть от:
- `ProductStatusTransitionValidator` — проверка допустимости перехода
- `ProductRepository` — загрузка и сохранение продукта
- `ProductStatusHistoryService` — запись истории
- `ProductStatusEventPublisher` — публикация событий (опционально)

### 4. Настройка Spring бинов
Убедиться, что все необходимые бины созданы и внедрены:
- `ProductStatusTransitionValidatorImpl` — уже должен быть `@Component`
- `ProductStatusTransitionServiceImpl` — `@Service`
- `ProductStatusHistoryServiceImpl` — `@Service`
- `ProductStatusEventPublisherImpl` — `@Component` (или заглушка)

### 5. Обновление ProductEntity (опционально)
Рассмотреть добавление поля `statusReason` в `ProductEntity` для хранения причины последнего изменения статуса. Это можно сделать позже, если требуется.

### 6. Создание REST контроллеров (если применимо)
Если в проекте есть REST API для продуктов, добавить endpoints:
- `PATCH /api/products/{id}/status` — изменение статуса
- `GET /api/products/{id}/possible-transitions` — получение возможных переходов

### 7. Миграция данных (если требуется)
Если в существующих записях продуктов поле `status` равно `null`, установить значение по умолчанию (например, `DRAFT`). Написать скрипт миграции.

## Последовательность вызовов при изменении статуса через ProductService

1. Клиент вызывает `productService.updateStatus(productId, targetStatus, reason)`
2. `ProductServiceImpl` делегирует вызов `statusTransitionService.transitionProduct(...)`
3. `ProductStatusTransitionServiceImpl`:
   - Загружает продукт из репозитория
   - Проверяет допустимость перехода через валидатор
   - Записывает переход в историю
   - Обновляет статус продукта
   - Сохраняет продукт
   - Публикует событие
   - Возвращает обновленный продукт
4. `ProductServiceImpl` возвращает результат клиенту

## Обработка ошибок
- `ProductNotFoundException` — если продукт не найден
- `InvalidStatusTransitionException` — если переход недопустим
- Общие исключения Spring Data (DataAccessException) — обрабатываются глобальным обработчиком

## Тестирование интеграции
Написать интеграционные тесты для `ProductServiceImpl` с фокусом на:
- Успешное изменение статуса
- Отказ при недопустимом переходе
- Корректность возвращаемых возможных переходов
- Сохранение истории

## Влияние на существующий код
- Изменения минимальны: только добавление новых методов в интерфейс и реализацию
- Существующие методы `deleteProduct` и `existsBySku` остаются без изменений
- Не требуется модификация репозитория или сущностей (кроме опционального добавления поля)

## График реализации
1. **День 1**: Расширение интерфейса и создание `ProductStatusTransitionServiceImpl`
2. **День 2**: Интеграция в `ProductServiceImpl` и настройка бинов
3. **День 3**: Написание интеграционных тестов
4. **День 4**: Рефакторинг и проверка на соответствие бизнес-требованиям

## Заключение
Интеграция системы управления статусами с существующим `ProductService` позволит:
- Использовать единую точку входа для всех операций с продуктами
- Сохранить обратную совместимость
- Обеспечить согласованность данных через транзакции
- Предоставить клиентам простой API для управления статусами