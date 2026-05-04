# MSA Product Processing

Микросервис для обработки продуктов (products) на базе Spring Boot и Kotlin.

## Технологии

- **Kotlin** 1.9.25
- **Spring Boot** 3.5.13
- **JDK** 17
- **PostgreSQL** — база данных
- **Spring Data JDBC** — ORM
- **Liquibase** — миграции схемы БД
- **KTlint** — линтинг Kotlin кода

## Основные возможности

- CRUD операции с продуктами
- Поддержка категорий и статусов продуктов
- Хранение тегов в формате JSONB
- Асинхронная обработка продуктов через ThreadPool
- Swagger/OpenAPI документация

## Структура проекта

```
src/main/kotlin/ru/example/product/processing/
├── MsaProductProcessingApplication.kt  # Точка входа
├── config/                              # Конфигурации
│   └── converter/                       # JSONB конвертеры
├── domain/                              # Domain модели
│   ├── ProductEntity.kt
│   ├── ProductCategory.kt
│   └── ProductStatus.kt
├── repository/                          # Репозитории
├── service/                             # Сервисы
└── exception/                           # Исключения
```

## Конфигурация

Переменные окружения:

| Переменная       | Описание              |
|------------------|-----------------------|
| `DB_URL`         | URL подключения к БД  |
| `DB_USER`        | Имя пользователя БД   |
| `DB_PASSWORD`    | Пароль пользователя БД|

Сервис запускается на порту **8082**.

### Thread Pool конфигурация

| Параметр                        | Значение |
|---------------------------------|----------|
| `core-size`                     | 4        |
| `max-size`                      | 4        |
| `queue-capacity`                | 256      |
| `keep-alive-seconds`            | 60       |

## Запуск

### Требования

- JDK 17+
- PostgreSQL
- Gradle 8+

### Команды

```bash
# Сборка проекта
./gradlew build

# Запуск приложения
./gradlew bootRun

# Запуск тестов
./gradlew test

# Проверка кода (KTlint)
./gradlew ktlintCheck
```

## API документация

Swagger UI доступен по адресу: `http://localhost:8082/swagger-ui.html`

OpenAPI спецификация: `http://localhost:8082/api-docs`

## Domain модель

### ProductEntity

| Поле            | Тип          | Описание              |
|-----------------|--------------|-----------------------|
| `id`            | UUID         | Уникальный идентификатор |
| `sku`           | String       | Артикул товара        |
| `name`          | String       | Название              |
| `description`   | String       | Описание              |
| `price`         | BigDecimal   | Цена                  |
| `quantity`      | Int          | Количество            |
| `weight`        | Double?      | Вес (опционально)    |
| `isAvailable`   | Boolean      | Доступность           |
| `status`        | ProductStatus| Статус                |
| `category`      | ProductCategory| Категория           |
| `tags`          | List<String> | Теги (JSONB)         |
| `createdAt`     | Instant      | Дата создания         |
| `updatedAt`     | Instant      | Дата обновления       |

### ProductStatus

Перечисление статусов продукта с кодами:

| Статус | Код | Описание |
|--------|-----|----------|
| DRAFT | 0 | Черновик продукта |
| PENDING_REVIEW | 10 | Ожидает проверки |
| REVIEWED | 20 | Проверено модератором |
| APPROVED | 30 | Утверждено менеджером |
| REJECTED | 40 | Отклонено |
| ACTIVE | 50 | Активно для продажи |
| PROCESSED | 60 | Обработано (заказ собран) |
| SHIPPED | 70 | Отправлено клиенту |
| ARCHIVED | 80 | Архивировано (завершено) |

### Система управления статусами

Реализована система валидации переходов между статусами, позволяющая продукту двигаться по предопределенным путям и комбинировать их на ходу.

**Компоненты:**
- `ProductStatusTransitions` — матрица допустимых переходов
- `ProductStatusTransitionValidator` — валидатор переходов
- `ProductStatusTransitionService` — сервис выполнения переходов с записью истории
- `ProductStatusHistoryService` — сервис истории изменений статуса
- `ProductStatusHistoryRecord` — сущность записи истории

**Основные пути:**
1. Успешный workflow: `DRAFT → PENDING_REVIEW → REVIEWED → APPROVED → ACTIVE → PROCESSED → SHIPPED → ARCHIVED`
2. Отклонение с исправлением: `DRAFT → PENDING_REVIEW → REVIEWED → REJECTED → PENDING_REVIEW → REVIEWED → APPROVED → ...`
3. Досрочное архивирование: `DRAFT → PENDING_REVIEW → ARCHIVED`
4. Восстановление из архива: `ARCHIVED → ACTIVE → PROCESSED → SHIPPED → ARCHIVED`

**API:**
- `ProductService.updateStatus(productId, targetStatus, reason?)` — изменить статус
- `ProductService.getPossibleTransitions(productId)` — получить возможные переходы
- `ProductStatusHistoryService.getHistory(productId)` — получить историю изменений

**Исключения:**
- `InvalidStatusTransitionException` — при попытке недопустимого перехода
- `ProductNotFoundException` — если продукт не найден

Подробная документация в директории `plans/`.
