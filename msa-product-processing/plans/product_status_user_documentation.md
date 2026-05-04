# Документация по использованию системы управления статусами продукта

## Обзор
Система управления статусами продукта позволяет контролировать жизненный цикл продукта через набор допустимых переходов между статусами. Продукт может двигаться по предопределенным путям или комбинировать их на ходу.

## Статусы продукта

| Статус | Код | Описание |
|--------|-----|----------|
| DRAFT | 0 | Черновик продукта (начальное состояние) |
| PENDING_REVIEW | 10 | Ожидает проверки модератором |
| REVIEWED | 20 | Проверено модератором, ожидает утверждения |
| APPROVED | 30 | Утверждено менеджером, готово к активации |
| REJECTED | 40 | Отклонено на этапе проверки |
| ACTIVE | 50 | Активно для продажи |
| PROCESSED | 60 | Заказ обработан (собран) |
| SHIPPED | 70 | Отправлено клиенту |
| ARCHIVED | 80 | Архивировано (завершено) |

## Основные пути (типовые сценарии)

### Путь 1: Успешный workflow
```
DRAFT → PENDING_REVIEW → REVIEWED → APPROVED → ACTIVE → PROCESSED → SHIPPED → ARCHIVED
```
**Использование**: Стандартный жизненный цикл продукта от создания до доставки.

### Путь 2: Отклонение с исправлением
```
DRAFT → PENDING_REVIEW → REVIEWED → REJECTED → PENDING_REVIEW → REVIEWED → APPROVED → ...
```
**Использование**: Продукт отклонен, исправлен и повторно отправлен на проверку.

### Путь 3: Досрочное архивирование
```
DRAFT → PENDING_REVIEW → ARCHIVED
```
**Использование**: Отмена продукта на этапе проверки.

### Путь 4: Снятие с продажи
```
... → ACTIVE → ARCHIVED
```
**Использование**: Активный продукт снят с продажи.

### Путь 5: Восстановление из архива
```
ARCHIVED → ACTIVE → PROCESSED → SHIPPED → ARCHIVED
```
**Использование**: Архивированный продукт восстановлен и снова продан.

## API использования

### 1. Изменение статуса продукта

**Метод**: `ProductService.updateStatus(productId, targetStatus, reason?)`

**Пример на Kotlin**:
```kotlin
// Внедрение сервиса
@Autowired
private lateinit var productService: ProductService

// Изменение статуса
val updatedProduct = productService.updateStatus(
    productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
    targetStatus = ProductStatus.APPROVED,
    reason = "Продукт соответствует всем требованиям"
)
```

**Пример на Java**:
```java
ProductEntity updatedProduct = productService.updateStatus(
    UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
    ProductStatus.APPROVED,
    "Продукт соответствует всем требованиям"
);
```

**Возвращает**: Обновленную сущность `ProductEntity` с новым статусом.

**Исключения**:
- `ProductNotFoundException` — если продукт не найден
- `InvalidStatusTransitionException` — если переход недопустим

### 2. Получение возможных переходов

**Метод**: `ProductService.getPossibleTransitions(productId)`

**Пример**:
```kotlin
val possibleTransitions = productService.getPossibleTransitions(productId)
// Возвращает Set<ProductStatus>, например: [PENDING_REVIEW, ARCHIVED]
```

**Использование**: Для построения UI, где пользователю показываются доступные действия (кнопки "Отправить на проверку", "Архивировать" и т.д.)

### 3. Получение истории статусов

**Метод**: `ProductStatusHistoryService.getHistory(productId)`

**Пример**:
```kotlin
val history = productStatusHistoryService.getHistory(productId)
history.forEach { record ->
    println("${record.timestamp}: ${record.fromStatus} → ${record.toStatus} (${record.reason})")
}
```

## REST API (если реализовано)

### Изменение статуса
```
PATCH /api/products/{id}/status
Content-Type: application/json

{
  "targetStatus": "APPROVED",
  "reason": "Продукт соответствует всем требованиям"
}
```

**Ответ**:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "sku": "PROD-001",
  "name": "Пример продукта",
  "status": "APPROVED",
  "updatedAt": "2026-05-03T18:40:00Z"
}
```

### Получение возможных переходов
```
GET /api/products/{id}/possible-transitions
```

**Ответ**:
```json
{
  "currentStatus": "DRAFT",
  "possibleTransitions": ["PENDING_REVIEW", "ARCHIVED"]
}
```

### Получение истории статусов
```
GET /api/products/{id}/status-history
```

**Ответ**:
```json
{
  "productId": "123e4567-e89b-12d3-a456-426614174000",
  "history": [
    {
      "fromStatus": "DRAFT",
      "toStatus": "PENDING_REVIEW",
      "timestamp": "2026-05-03T18:30:00Z",
      "reason": "Отправлен на проверку"
    },
    {
      "fromStatus": "PENDING_REVIEW",
      "toStatus": "REVIEWED",
      "timestamp": "2026-05-03T18:35:00Z",
      "reason": "Проверен модератором"
    }
  ]
}
```

## Бизнес-правила

### 1. Допустимые переходы
Переходы разрешены только согласно матрице:

| Из статуса | В статус (допустимые) |
|------------|----------------------|
| DRAFT | PENDING_REVIEW, ARCHIVED |
| PENDING_REVIEW | REVIEWED, ARCHIVED, DRAFT |
| REVIEWED | APPROVED, REJECTED |
| APPROVED | ACTIVE, ARCHIVED |
| REJECTED | PENDING_REVIEW, ARCHIVED |
| ACTIVE | PROCESSED, ARCHIVED |
| PROCESSED | SHIPPED, ARCHIVED |
| SHIPPED | ARCHIVED |
| ARCHIVED | ACTIVE |

### 2. Дополнительные ограничения
- **Возврат в черновик после утверждения запрещен**: После перехода в `APPROVED` нельзя вернуться в `DRAFT`.
- **Многократное восстановление из архива ограничено**: Продукт может быть восстановлен из `ARCHIVED` не более 3 раз (настраивается).
- **Минимальное время в статусе**: Некоторые статусы требуют минимального времени пребывания (например, 24 часа в `PENDING_REVIEW`) перед переходом.

## Интеграция с другими системами

### События
При изменении статуса продукта система публикует событие `ProductStatusChangedEvent`. Подписчики могут:
- Отправлять уведомления пользователям
- Обновлять данные в поисковом индексе
- Запускать бизнес-процессы (например, списание со склада при переходе в `PROCESSED`)

### Конфигурация событий
По умолчанию события публикуются через Spring ApplicationEvent. Для интеграции с внешними системами (Kafka, RabbitMQ) необходимо реализовать `ProductStatusEventPublisher`.

## Примеры использования

### Пример 1: Создание и отправка продукта на проверку
```kotlin
// Создание продукта (через репозиторий)
val product = ProductEntity(
    sku = "PROD-001",
    name = "Новый продукт",
    status = ProductStatus.DRAFT,
    // ... другие поля
)
val savedProduct = productRepository.save(product)

// Отправка на проверку
productService.updateStatus(savedProduct.id!!, ProductStatus.PENDING_REVIEW, "Готов к проверке")
```

### Пример 2: Обработка отклоненного продукта
```kotlin
// Предположим, продукт уже в статусе REJECTED
val possible = productService.getPossibleTransitions(productId)
// possible = [PENDING_REVIEW, ARCHIVED]

// Исправляем и отправляем повторно
productService.updateStatus(productId, ProductStatus.PENDING_REVIEW, "Исправлены замечания")
```

### Пример 3: Полный жизненный цикл с комбинированием
```kotlin
// Последовательность переходов с комбинированием путей
val transitions = listOf(
    ProductStatus.DRAFT to ProductStatus.PENDING_REVIEW,
    ProductStatus.PENDING_REVIEW to ProductStatus.REVIEWED,
    ProductStatus.REVIEWED to ProductStatus.REJECTED,
    ProductStatus.REJECTED to ProductStatus.PENDING_REVIEW,
    ProductStatus.PENDING_REVIEW to ProductStatus.REVIEWED,
    ProductStatus.REVIEWED to ProductStatus.APPROVED,
    ProductStatus.APPROVED to ProductStatus.ACTIVE,
    ProductStatus.ACTIVE to ProductStatus.ARCHIVED
)

transitions.forEach { (from, to) ->
    // На практике нужно проверять текущий статус
    productService.updateStatus(productId, to, "Переход по workflow")
}
```

## Отладка и мониторинг

### Логирование
Система логирует все переходы статусов на уровне INFO. Пример лога:
```
2026-05-03 18:40:00 INFO  ProductStatusTransitionServiceImpl - Transition product 123e4567... from DRAFT to PENDING_REVIEW
2026-05-03 18:45:00 INFO  ProductStatusTransitionServiceImpl - Transition product 123e4567... from PENDING_REVIEW to REVIEWED
```

### Метрики
Доступны метрики:
- `product.status.transitions.total` — общее количество переходов
- `product.status.transitions.error` — количество неудачных переходов
- `product.status.duration.{status}` — время пребывания в статусе

### Визуализация истории
Используйте endpoint `/api/products/{id}/status-history` для получения истории переходов и построения timeline в UI.

## Часто задаваемые вопросы

### Вопрос: Можно ли пропустить статус?
**Ответ**: Нет, переходы должны следовать матрице. Например, нельзя перейти из `DRAFT` сразу в `APPROVED`, нужно пройти через `PENDING_REVIEW` и `REVIEWED`.

### Вопрос: Что происходит при попытке недопустимого перехода?
**Ответ**: Выбрасывается `InvalidStatusTransitionException` с описанием ошибки. Переход не выполняется, статус продукта не меняется.

### Вопрос: Как добавить новый статус?
**Ответ**:
1. Добавить значение в enum `ProductStatus`
2. Обновить матрицу переходов в `ProductStatusTransitions`
3. При необходимости добавить бизнес-правила
4. Обновить документацию и тесты

### Вопрос: Как отменить переход?
**Ответ**: Прямой отмены перехода нет. Можно выполнить обратный переход, если он разрешен матрицей (например, из `PENDING_REVIEW` в `DRAFT`). В противном случае требуется административное вмешательство.

## Поддержка
Для вопросов и проблем обращайтесь:
- Разработчики: команда разработки продукта
- Документация: `/plans/product_status_workflow.md`
- Исходный код: пакет `ru.example.product.processing.service.status`