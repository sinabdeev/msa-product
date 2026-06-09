# AGENTS.md — Руководство для ИИ-агентов

> Этот файл содержит правила, гайдлайны и контекст проекта для ИИ-агентов, работающих с кодовой базой **msa-product-data**.

---

## 1. Обзор проекта

**msa-product-data** — микросервис для управления продуктами, построенный на Spring Boot с использованием Kotlin и PostgreSQL. Сервис предоставляет REST API для CRUD операций над продуктами с поддержкой пакетного создания и параллельной обработки.

| Параметр | Значение |
|----------|----------|
| Имя проекта | `msa-product-data` |
| Группа | `ru.example` |
| Версия | `0.0.1` |
| Корневой пакет | `ru.example.product.data` |

---

## 2. Технологический стек

| Категория | Технология | Версия |
|-----------|------------|--------|
| Язык | Kotlin | 1.9.25 |
| Фреймворк | Spring Boot | 3.5.13 |
| JVM | Java | 17 |
| База данных | PostgreSQL | 15+ |
| Миграции БД | Liquibase | - |
| ORM | Spring Data JDBC | - |
| Документация API | SpringDoc OpenAPI | 2.5.0 |
| Валидация | Jakarta Bean Validation | 3.0 |
| Сериализация | Jackson + JSONB | - |
| Сборка | Gradle (Kotlin DSL) | - |
| Линтер | ktlint | 12.1.0 |
| Тестирование | JUnit 5 + Spring Boot Test + H2 | - |

---

## 3. Структура проекта

```
msa-product-data/
├── build.gradle.kts                    # Конфигурация сборки
├── settings.gradle.kts                 # Настройки проекта
├── AGENTS.md                           # Это файл
├── README.md                           # Документация проекта
├── HELP.md                             # Справочная документация
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── src/main/kotlin/ru/example/product/data/
│   ├── MsaProductReceiverApplication.kt  # Точка входа
│   ├── config/
│   │   ├── JdbcConfig.kt               # Конфигурация JDBC
│   │   ├── ThreadPoolConfig.kt         # Пул потоков для async
│   │   ├── OpenApiConfig.kt            # Конфигурация Swagger
│   │   └── converter/
│   │       └── JsonbConverters.kt      # JSONB конвертеры
│   ├── controller/
│   │   ├── ProductController.kt        # REST контроллер
│   │   └── exception/
│   │       └── GlobalExceptionHandler.kt # Обработка ошибок
│   ├── domain/
│   │   ├── ProductEntity.kt            # JPA сущность
│   │   ├── ProductDto.kt               # DTO для ответов
│   │   ├── ProductStatus.kt            # Статус продукта (enum)
│   │   ├── ProductCategory.kt          # Категория продукта (enum)
│   │   └── TagsWrapper.kt              # Обёртка для JSONB тегов
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CreateProductRequest.kt
│   │   │   ├── CreateProductsBatchRequest.kt
│   │   │   └── UpdateProductRequest.kt
│   │   ├── response/
│   │   │   ├── ApiResponse.kt
│   │   │   └── BatchSaveResult.kt
│   │   └── error/
│   │       └── ApiError.kt
│   ├── exception/
│   │   └── ProductNotFoundException.kt # Кастомный exception
│   ├── mappers/
│   │   └── ProductMapper.kt            # Маппинг Entity <-> DTO
│   ├── repository/
│   │   └── ProductRepository.kt        # Spring Data JDBC репозиторий
│   └── service/
│       ├── ProductService.kt           # Интерфейс сервиса
│       ├── ProductServiceImpl.kt        # Реализация сервиса
│       └── ProductHandler.kt           # Async обработчик
├── src/main/resources/
│   ├── application.yaml                # Основной конфиг
│   ├── db/changelog/
│   │   ├── db.changelog-master.yaml    # Master changelog Liquibase
│   │   └── changes/
│   │       ├── 001-initial-schema.yaml
│   │       ├── 002-add-status-column.yaml
│   │       └── 003-add-product-status-history.yaml
│   ├── openapi.yaml                    # OpenAPI спецификация
│   └── Product API.postman_collection.json
└── src/test/kotlin/ru/example/product/data/
    ├── MsaProductReceiverApplicationTests.kt
    ├── controller/
    │   └── ProductControllerTest.kt
    └── service/
        └── (тесты сервисов)
```

---

## 4. Основные сущности

### ProductEntity

```kotlin
@Table("products")
data class ProductEntity(
    val id: UUID?,
    val sku: String,              // Уникальный артикул (NotBlank, max 50)
    val name: String,             // Название (NotBlank, max 255)
    val description: String,      // Описание (NotBlank)
    val price: BigDecimal,        // Цена (> 0)
    val quantity: Int,            // Количество (>= 0)
    val weight: Double?,          // Вес (nullable)
    val isAvailable: Boolean,     // Флаг доступности
    val status: ProductStatus?,   // Статус продукта
    val category: ProductCategory,// Категория
    val tags: TagsWrapper,        // JSONB массив тегов
    val createdAt: Instant?,      // Время создания
    val updatedAt: Instant?,      // Время обновления
)
```

### ProductStatus (enum)

| Статус | Код | Описание |
|--------|-----|----------|
| DRAFT | 0 | Черновик |
| PENDING_REVIEW | 10 | На проверке |
| REVIEWED | 20 | Проверен |
| APPROVED | 30 | Одобрено |
| REJECTED | 40 | Отклонено |
| ACTIVE | 50 | Активен |
| PROCESSED | 60 | Обработан |
| SHIPPED | 70 | Отправлен |
| ARCHIVED | 80 | Архивирован |

### ProductCategory (enum)

`ELECTRONICS`, `CLOTHING`, `FOOD`, `HOME_GOODS`, `APPLIANCES`, `SMART_HOME`

---

## 5. API Endpoints

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/v1/products` | Создать продукт |
| POST | `/api/v1/products/batch` | Пакетное создание |
| GET | `/api/v1/products` | Получить список |
| GET | `/api/v1/products/{id}` | Получить по ID |
| PUT | `/api/v1/products/{id}` | Обновить продукт |
| DELETE | `/api/v1/products/{id}` | Удалить продукт |
| GET | `/api-docs` | Swagger UI |

### Формат ответа

```json
{
  "success": true,
  "message": "Product created successfully",
  "data": { ... },
  "timestamp": "2023-01-01T12:00:00Z"
}
```

---

## 6. Правила и гайдлайны для ИИ-агентов

### 6.1. Стиль кода Kotlin

- **Используй data class** для DTO и сущностей
- **Используй type inference** (`val` вместо явного типа когда очевидно)
- **Используй null-safety**: `?`, `?:`, `!!` только когда абсолютно необходимо
- **Используй string templates**: `"Product: $name"` вместо конкатенации
- **Используй when** вместо switch
- **Используй scope functions**: `let`, `apply`, `run`, `also` где уместно
- **Используй extension functions** для переиспользуемой логики
- **Используй sealed classes** для иерархий типов

### 6.2.命名约定 (Naming Conventions)

| Элемент | Convention | Пример |
|---------|------------|--------|
| Классы | PascalCase | `ProductController` |
| Интерфейсы | PascalCase | `ProductService` |
| Методы | camelCase | `createProduct()` |
| Переменные | camelCase | `productEntity` |
| Константы | SCREAMING_SNAKE_CASE | `MAX_BATCH_SIZE` |
| Пакеты | lowercase.dot.separated | `ru.example.product.data` |
| DTO | PascalCase + суффикс | `ProductDto`, `CreateProductRequest` |
| Entity | PascalCase + суффикс | `ProductEntity` |

### 6.3. Архитектурные паттерны

- **Layered Architecture**: Controller -> Service -> Repository
- **DTO Pattern**: Разделение внешних и внутренних моделей
- **Mapper Pattern**: Трансформация Entity <-> DTO
- **Interface Segregation**: Интерфейс `ProductService` + реализация `ProductServiceImpl`
- **Async Processing**: `@Async` для параллельной обработки через `ThreadPoolConfig`
- **Standardized Response**: `ApiResponse<T>` для всех ответов API

### 6.4. Работа с базой данных

- Используй **Spring Data JDBC** интерфейсы (не JPA)
- Миграции через **Liquibase** (YAML формат)
- Каждый файл миграции в `src/main/resources/db/changelog/changes/`
- Добавляй новый changelog файл в `db.changelog-master.yaml`
- Для JSONB полей используй **конвертеры** (`@WritingConverter`, `@ReadingConverter`)
- **Не используй** `@Entity`, `@GeneratedValue` — Spring Data JDBC не требует их

### 6.5. Валидация

- Используй **Jakarta Validation** аннотации в DTO:
  - `@NotBlank` для строк
  - `@NotNull` для nullable полей
  - `@Size` для ограничений длины
  - `@DecimalMin` / `@DecimalMax` для чисел
  - `@Min` / `@Max` для целых чисел
- Валидация через `@Valid` в контроллере

### 6.6. Обработка ошибок

- Используй **GlobalExceptionHandler** для централизованной обработки
- Кастомные исключения: `ProductNotFoundException`
- HTTP статусы:
  - `201` — Created
  - `200` — OK
  - `400` — Bad Request (валидация)
  - `404` — Not Found
  - `409` — Conflict (дубликат SKU)
  - `500` — Internal Server Error

### 6.7. Логирование

- Используй **SLF4J** (`LoggerFactory`)
- Используй **placeholder** вместо string concatenation: `logger.info("Product: {}", sku)`
- Уровни логирования:
  - `INFO` — основные операции
  - `DEBUG` — отладочная информация
  - `WARN` — предупреждения
  - `ERROR` — ошибки
- Используй **MDC** для трассировки запросов (correlationId)

### 6.8. Конфигурация

- Все внешние параметры через `${VAR}` или `@Value`
- Пул потоков конфигурируется в `application.yaml`:
  ```yaml
  product:
    batch:
      thread-pool:
        core-size: 4
        max-size: 4
        queue-capacity: 256
        keep-alive-seconds: 60
        thread-name-prefix: "product-save-"
        allow-core-thread-timeout: true
  ```

### 6.9. Тестирование

- Используй **JUnit 5** + **Spring Boot Test**
- Для интеграционных тестов используй **H2** (in-memory)
- Тестовый конфиг в `src/test/resources/application-test.yaml`
- Mock сервисов через `@MockBean`
- Slice аннотации: `@WebMvcTest`, `@DataJdbcTest`

### 6.10. OpenAPI / Swagger

- Аннотируй контроллеры через `@Tag`, `@Operation`, `@ApiResponses`
- Аннотируй DTO через `@Schema`
- Документация доступна по `/swagger-ui.html`

---

## 7. Команды для работы с проектом

```bash
# Сборка проекта
./gradlew build

# Запуск приложения
./gradlew bootRun

# Запуск тестов
./gradlew test

# Проверка кода ktlint
./gradlew ktlintCheck

# Форматирование кода
./gradlew ktlintFormat

# Генерация OpenAPI спецификации
./gradlew openApiDocs
```

---

## 8. Важные файлы для справки

| Файл | Назначение |
|------|------------|
| [`build.gradle.kts`](build.gradle.kts) | Зависимости и конфигурация сборки |
| [`settings.gradle.kts`](settings.gradle.kts) | Имя проекта |
| [`application.yaml`](src/main/resources/application.yaml) | Конфигурация приложения |
| [`MsaProductReceiverApplication.kt`](src/main/kotlin/ru/example/product/data/MsaProductReceiverApplication.kt) | Точка входа, `@EnableAsync` |
| [`ProductController.kt`](src/main/kotlin/ru/example/product/data/controller/ProductController.kt) | REST API endpoints |
| [`ProductService.kt`](src/main/kotlin/ru/example/product/data/service/ProductService.kt) | Интерфейс сервиса |
| [`ProductServiceImpl.kt`](src/main/kotlin/ru/example/product/data/service/ProductServiceImpl.kt) | Реализация сервиса |
| [`ProductHandler.kt`](src/main/kotlin/ru/example/product/data/service/ProductHandler.kt) | Async обработчик |
| [`ProductRepository.kt`](src/main/kotlin/ru/example/product/data/repository/ProductRepository.kt) | Репозиторий |
| [`ProductMapper.kt`](src/main/kotlin/ru/example/product/data/mappers/ProductMapper.kt) | Маппинг Entity <-> DTO |
| [`ThreadPoolConfig.kt`](src/main/kotlin/ru/example/product/data/config/ThreadPoolConfig.kt) | Конфигурация пула потоков |
| [`JsonbConverters.kt`](src/main/kotlin/ru/example/product/data/config/converter/JsonbConverters.kt) | JSONB конвертеры |
| [`GlobalExceptionHandler.kt`](src/main/kotlin/ru/example/product/data/controller/exception/GlobalExceptionHandler.kt) | Обработка ошибок |
| [`db.changelog-master.yaml`](src/main/resources/db/changelog/db.changelog-master.yaml) | Master changelog Liquibase |

---

## 9. Чеклист при внесении изменений

При внесении изменений в проект проверь:

- [ ] Код соответствует стилю ktlint
- [ ] Добавлены валидационные аннотации в DTO
- [ ] Добавлены аннотации `@Schema` для OpenAPI
- [ ] Добавлено логирование (INFO/DEBUG/WARN)
- [ ] Обработаны edge cases и ошибки
- [ ] Написаны unit/integration тесты
- [ ] Обновлена документация (README, OpenAPI)
- [ ] Миграции БД добавлены в changelog
- [ ] Нет утечек ресурсов (connections, threads)
- [ ] Thread-safe код для async операций

---

## 10. Common Tasks для ИИ-агентов

### Добавление нового поля

1. Добавить в [`ProductEntity.kt`](src/main/kotlin/ru/example/product/data/domain/ProductEntity.kt)
2. Добавить в [`ProductDto.kt`](src/main/kotlin/ru/example/product/data/domain/ProductDto.kt)
3. Обновить [`ProductMapper.kt`](src/main/kotlin/ru/example/product/data/mappers/ProductMapper.kt)
4. Обновить DTO запросов/ответов
5. Добавить миграцию Liquibase
6. Обновить тесты

### Добавление нового API endpoint

1. Добавить метод в [`ProductController.kt`](src/main/kotlin/ru/example/product/data/controller/ProductController.kt)
2. Добавить метод в [`ProductService.kt`](src/main/kotlin/ru/example/product/data/service/ProductService.kt)
3. Реализовать в [`ProductServiceImpl.kt`](src/main/kotlin/ru/example/product/data/service/ProductServiceImpl.kt)
4. Добавить аннотации `@Operation`, `@ApiResponses`
5. Добавить DTO для запроса/ответа
6. Добавить тесты

### Добавление миграции БД

1. Создать файл в [`db/changelog/changes/`](src/main/resources/db/changelog/changes/)
2. Добавить ссылку в [`db.changelog-master.yaml`](src/main/resources/db/changelog/db.changelog-master.yaml)
3. Обновить [`ProductEntity.kt`](src/main/kotlin/ru/example/product/data/domain/ProductEntity.kt)
4. Обновить [`JdbcConfig.kt`](src/main/kotlin/ru/example/product/data/config/JdbcConfig.kt) если нужны конвертеры

---

## 11. Package Structure Reference

```
ru.example.product.data
├── config              # Spring configurations
├── controller          # REST controllers + exception handling
├── domain              # Entities, DTOs, Enums
├── dto                 # Request/Response DTOs
│   ├── error
│   └── request
│   └── response
├── exception           # Custom exceptions
├── mappers             # Entity <-> DTO mappers
├── repository          # Spring Data repositories
└── service             # Business logic
```

---

## 12. Environment Variables

| Переменная | Описание | Обязательная |
|------------|----------|:-:|
| `DB_URL` | JDBC URL PostgreSQL | Да |
| `DB_USER` | Пользователь БД | Да |
| `DB_PASSWORD` | Пароль БД | Да |

---

*Файл создан для ИИ-агентов. Последнее обновление: 2026-05-28*
