# План реализации API истории статусов продуктов

## 1. Цель

Создать REST API endpoint для получения записей из таблицы `product.product_status_history`, который будет использоваться React Dashboard для визуализации данных в реальном времени.

---

## 2. Требуемый API для фронтенда

Согласно [`frontend-impl-plan.md`](plans/frontend-impl-plan.md), фронтенд ожидает:

| Метод | Путь | Назначение |
|-------|------|------------|
| GET | `/api/v1/status-history?limit=1000` | Первичная загрузка (последние 1000 записей) |
| GET | `/api/v1/status-history?created_after=2026-06-03T20:19:45&limit=100` | Поллинг новых данных (после указанного timestamp) |

### Формат ответа

```json
{
  "success": true,
  "message": "Status history retrieved successfully",
  "data": [
    {
      "id": "123e4567-e89b-12d3-a456-426614174001",
      "productId": "123e4567-e89b-12d3-a456-426614174002",
      "fromStatus": "DRAFT",
      "toStatus": "PENDING_REVIEW",
      "reason": "Batch processing",
      "userId": "123e4567-e89b-12d3-a456-426614174003",
      "createdAt": "2026-06-03T20:19:45.000Z",
      "processingDurationSeconds": 12345
    }
  ],
  "timestamp": "2026-06-09T18:00:00Z"
}
```

---

## 3. Архитектура решения

```mermaid
flowchart LR
    FC[Frontend Dashboard] -->|GET /api/v1/status-history| PC[ProductStatusHistoryController]
    PC --> PHS[ProductStatusHistoryService]
    PHS --> PSR[ProductStatusHistoryRepository]
    PSR -->|SQL| PG[(PostgreSQL)]
    PG -->|rows| PSR
    PSR -->|Entity[]| PHS
    PHS -->|Dto[]| PC
    PC -->|JSON| FC
```

---

## 4. Структура БД (уже создана миграцией 003)

```sql
CREATE TABLE product.product_status_history (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    product_id uuid NOT NULL,
    from_status text NOT NULL,
    to_status text NOT NULL,
    reason text NULL,
    user_id uuid NULL,
    created_at timestamp DEFAULT now() NOT NULL,
    processing_duration_seconds bigint NULL,
    CONSTRAINT product_status_history_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_product_status_history_product_id ON product.product_status_history(product_id);
```

---

## 5. Пошаговый план реализации

### Шаг 1: Создание сущности ProductStatusHistoryEntity

**Файл:** [`src/main/kotlin/ru/example/product/data/domain/ProductStatusHistoryEntity.kt`](src/main/kotlin/ru/example/product/data/domain/ProductStatusHistoryEntity.kt)

**Новый файл.** Spring Data JDBC entity для таблицы `product.product_status_history`.

```kotlin
package ru.example.product.data.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.*

@Table("product_status_history")
data class ProductStatusHistoryEntity(
    @Id
    val id: UUID? = null,
    @Column("product_id")
    val productId: UUID,
    @Column("from_status")
    val fromStatus: String,
    @Column("to_status")
    val toStatus: String,
    @Column("reason")
    val reason: String? = null,
    @Column("user_id")
    val userId: UUID? = null,
    @Column("created_at")
    val createdAt: Instant,
    @Column("processing_duration_seconds")
    val processingDurationSeconds: Long? = null,
)
```

**Обоснование:**
- Используем `String` для `fromStatus`/`toStatus`, а не `ProductStatus` enum — в истории могут быть любые строковые значения, включая будущие статусы
- `processingDurationSeconds` как `Long?` — соответствует `bigint` в PostgreSQL
- Таблица в схеме `product`, поэтому `@Table("product_status_history")` — Spring Data JDBC автоматически добавит схему из конфигурации

---

### Шаг 2: Создание DTO ProductStatusHistoryDto

**Файл:** [`src/main/kotlin/ru/example/product/data/domain/ProductStatusHistoryDto.kt`](src/main/kotlin/ru/example/product/data/domain/ProductStatusHistoryDto.kt)

**Новый файл.** DTO для передачи данных фронтенду.

```kotlin
package ru.example.product.data.domain

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class ProductStatusHistoryDto(
    @Schema(description = "ID записи истории", example = "123e4567-e89b-12d3-a456-426614174001")
    val id: String,

    @Schema(description = "ID продукта", example = "123e4567-e89b-12d3-a456-426614174002")
    val productId: String,

    @Schema(description = "Статус до перехода", example = "DRAFT")
    val fromStatus: String,

    @Schema(description = "Статус после перехода", example = "PENDING_REVIEW")
    val toStatus: String,

    @Schema(description = "Причина перехода", example = "Batch processing")
    val reason: String?,

    @Schema(description = "ID пользователя", example = "123e4567-e89b-12d3-a456-426614174003")
    val userId: String?,

    @Schema(description = "Время создания записи", example = "2026-06-03T20:19:45.000Z")
    val createdAt: Instant,

    @Schema(description = "Длительность обработки в секундах", example = "12345")
    val processingDurationSeconds: Long?
)
```

**Обоснование:**
- `id` и `productId` как `String` — фронтенд ожидает строки для UUID
- `createdAt` как `Instant` — Jackson автоматически сериализует в ISO-8601 формат
- `@Schema` аннотации для OpenAPI документации

---

### Шаг 3: Создание DTO запроса ProductStatusHistoryQueryRequest

**Файл:** [`src/main/kotlin/ru/example/product/data/dto/request/StatusHistoryQueryRequest.kt`](src/main/kotlin/ru/example/product/data/dto/request/StatusHistoryQueryRequest.kt)

**Новый файл.** DTO для параметров запроса.

```kotlin
package ru.example.product.data.dto.request

import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import org.springframework.format.annotation.DateTimeFormat
import java.time.Instant

data class StatusHistoryQueryRequest(
    @Parameter(description = "Максимальное количество записей для возврата", example = "1000")
    @Schema(defaultValue = "1000")
    @Min(1)
    @Max(10000)
    val limit: Int = 1000,

    @Parameter(description = "Возвращать записи после этого timestamp (inclusive)")
    @Schema(example = "2026-06-03T20:19:45")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val createdAfter: Instant? = null,

    @Parameter(description = "ID продукта для фильтрации")
    @Schema(example = "123e4567-e89b-12d3-a456-426614174002")
    val productId: String? = null,

    @Parameter(description = "Фильтр по статусу после перехода")
    @Schema(example = "ACTIVE")
    val toStatus: String? = null
)
```

**Обоснование:**
- `limit` по умолчанию 1000 (как ожидает фронтенд)
- `createdAfter` — nullable, используется для поллинга
- `productId` — опциональная фильтрация по продукту
- `toStatus` — опциональная фильтрация по статусу
- Валидация через Jakarta: `min(1)`, `max(10000)`

---

### Шаг 4: Создание репозитория ProductStatusHistoryRepository

**Файл:** [`src/main/kotlin/ru/example/product/data/repository/ProductStatusHistoryRepository.kt`](src/main/kotlin/ru/example/product/data/repository/ProductStatusHistoryRepository.kt)

**Новый файл.** Spring Data JDBC репозиторий.

```kotlin
package ru.example.product.data.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.example.product.data.domain.ProductStatusHistoryEntity
import java.time.Instant
import java.util.*

@Repository
interface ProductStatusHistoryRepository : CrudRepository<ProductStatusHistoryEntity, UUID> {

    /**
     * Найти последние N записей, отсортированные по времени создания (новые первые).
     */
    fun findTopByOrderByCreatedAtDesc(limit: Int): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после указанного timestamp.
     */
    fun findByCreatedAtAfterOrderByCreatedAtDesc(createdAfter: Instant, limit: Int): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после timestamp для конкретного продукта.
     */
    fun findByProductIdAndCreatedAtAfterOrderByCreatedAtDesc(
        productId: UUID,
        createdAfter: Instant,
        limit: Int
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи после timestamp с фильтром по to_status.
     */
    fun findByCreatedAtAfterAndToStatusOrderByCreatedAtDesc(
        createdAfter: Instant,
        toStatus: String,
        limit: Int
    ): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта.
     */
    fun findByProductIdOrderByCreatedAtDesc(productId: UUID, limit: Int): List<ProductStatusHistoryEntity>

    /**
     * Найти записи для конкретного продукта с фильтром по to_status.
     */
    fun findByProductIdAndToStatusOrderByCreatedAtDesc(
        productId: UUID,
        toStatus: String,
        limit: Int
    ): List<ProductStatusHistoryEntity>
}
```

**Обоснование:**
- Spring Data JDBC автоматически генерирует SQL из имен методов
- Все методы возвращают записи, отсортированные по `created_at DESC`
- Комбинации методов покрывают все варианты фильтрации

---

### Шаг 5: Создание маппера ProductStatusHistoryMapper

**Файл:** [`src/main/kotlin/ru/example/product/data/mappers/ProductStatusHistoryMapper.kt`](src/main/kotlin/ru/example/product/data/mappers/ProductStatusHistoryMapper.kt)

**Новый файл.** Трансформация Entity <-> DTO.

```kotlin
package ru.example.product.data.mappers

import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.domain.ProductStatusHistoryEntity

object ProductStatusHistoryMapper {

    fun toDto(entity: ProductStatusHistoryEntity): ProductStatusHistoryDto {
        return ProductStatusHistoryDto(
            id = entity.id?.toString() ?: "",
            productId = entity.productId.toString(),
            fromStatus = entity.fromStatus,
            toStatus = entity.toStatus,
            reason = entity.reason,
            userId = entity.userId?.toString(),
            createdAt = entity.createdAt,
            processingDurationSeconds = entity.processingDurationSeconds
        )
    }

    fun toDto(entities: List<ProductStatusHistoryEntity>): List<ProductStatusHistoryDto> {
        return entities.map { toDto(it) }
    }
}
```

---

### Шаг 6: Создание сервиса ProductStatusHistoryService

**Файл:** [`src/main/kotlin/ru/example/product/data/service/ProductStatusHistoryService.kt`](src/main/kotlin/ru/example/product/data/service/ProductStatusHistoryService.kt)

**Новый файл.** Интерфейс сервиса.

```kotlin
package ru.example.product.data.service

import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.dto.request.StatusHistoryQueryRequest

interface ProductStatusHistoryService {

    /**
     * Получить записи истории статусов с фильтрацией.
     */
    fun getStatusHistory(query: StatusHistoryQueryRequest): List<ProductStatusHistoryDto>
}
```

**Файл:** [`src/main/kotlin/ru/example/product/data/service/ProductStatusHistoryServiceImpl.kt`](src/main/kotlin/ru/example/product/data/service/ProductStatusHistoryServiceImpl.kt)

**Новый файл.** Реализация сервиса.

```kotlin
package ru.example.product.data.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.dto.request.StatusHistoryQueryRequest
import ru.example.product.data.mappers.ProductStatusHistoryMapper
import ru.example.product.data.repository.ProductStatusHistoryRepository
import java.util.*

@Service
class ProductStatusHistoryServiceImpl(
    private val productStatusHistoryRepository: ProductStatusHistoryRepository,
) : ProductStatusHistoryService {

    private val logger: Logger = LoggerFactory.getLogger(ProductStatusHistoryServiceImpl::class.java)

    override fun getStatusHistory(query: StatusHistoryQueryRequest): List<ProductStatusHistoryDto> {
        logger.info("Fetching status history: limit={}, createdAfter={}, productId={}, toStatus={}",
            query.limit, query.createdAfter, query.productId, query.toStatus)

        val entities = when {
            query.productId != null && query.toStatus != null ->
                productStatusHistoryRepository.findByProductIdAndToStatusOrderByCreatedAtDesc(
                    UUID.fromString(query.productId), query.toStatus, query.limit
                )
            query.productId != null ->
                productStatusHistoryRepository.findByProductIdOrderByCreatedAtDesc(
                    UUID.fromString(query.productId), query.limit
                )
            query.toStatus != null && query.createdAfter != null ->
                productStatusHistoryRepository.findByCreatedAtAfterAndToStatusOrderByCreatedAtDesc(
                    query.createdAfter, query.toStatus, query.limit
                )
            query.toStatus != null ->
                productStatusHistoryRepository.findByCreatedAtAfterOrderByCreatedAtDesc(
                    query.createdAfter ?: Instant.EPOCH, query.toStatus, query.limit
                )
            query.createdAfter != null ->
                productStatusHistoryRepository.findByCreatedAtAfterOrderByCreatedAtDesc(
                    query.createdAfter, query.limit
                )
            else ->
                productStatusHistoryRepository.findTopByOrderByCreatedAtDesc(query.limit)
        }

        logger.info("Found {} status history records", entities.size)
        return ProductStatusHistoryMapper.toDto(entities)
    }
}
```

---

### Шаг 7: Создание контроллера ProductStatusHistoryController

**Файл:** [`src/main/kotlin/ru/example/product/data/controller/StatusHistoryController.kt`](src/main/kotlin/ru/example/product/data/controller/StatusHistoryController.kt)

**Новый файл.** REST контроллер.

```kotlin
package ru.example.product.data.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.example.product.data.domain.ProductStatusHistoryDto
import ru.example.product.data.dto.request.StatusHistoryQueryRequest
import ru.example.product.data.dto.response.ApiResponse
import ru.example.product.data.service.ProductStatusHistoryService
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RestController
@RequestMapping("/api/v1/status-history")
@Tag(name = "Status History", description = "Product status history API")
class StatusHistoryController(
    private val productStatusHistoryService: ProductStatusHistoryService,
) {
    @Operation(summary = "Get product status history records")
    @ApiResponses(
        SwaggerApiResponse(
            responseCode = "200",
            description = "Status history retrieved successfully",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ApiResponse::class),
                ),
            ],
        ),
        SwaggerApiResponse(responseCode = "400", description = "Invalid query parameters"),
    )
    @GetMapping
    fun getStatusHistory(
        @Parameter(description = "Максимальное количество записей", example = "1000")
        @RequestParam(required = false, defaultValue = "1000") limit: Int,

        @Parameter(description = "Возвращать записи после этого timestamp", example = "2026-06-03T20:19:45")
        @RequestParam(required = false) createdAfter: String?,

        @Parameter(description = "ID продукта для фильтрации", example = "123e4567-e89b-12d3-a456-426614174002")
        @RequestParam(required = false) productId: String?,

        @Parameter(description = "Фильтр по статусу после перехода", example = "ACTIVE")
        @RequestParam(required = false) toStatus: String?,
    ): ResponseEntity<ApiResponse<List<ProductStatusHistoryDto>>> {

        val createdAfterInstant = createdAfter?.let {
            try {
                java.time.Instant.parse(it)
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid createdAfter format: $it. Expected ISO-8601 format.")
            }
        }

        val query = StatusHistoryQueryRequest(
            limit = limit,
            createdAfter = createdAfterInstant,
            productId = productId,
            toStatus = toStatus
        )

        val records = productStatusHistoryService.getStatusHistory(query)
        val response = ApiResponse.success("Status history retrieved successfully", records)
        return ResponseEntity.ok(response)
    }
}
```

**Обоснование:**
- Endpoint `/api/v1/status-history` — соответствует ожиданию фронтенда
- Параметры как `@RequestParam` — фронтенд использует query params
- `createdAfter` приходит как строка, парсим в `Instant`
- `limit` по умолчанию 1000 — соответствует ожиданию фронтенда

---

### Шаг 8: Проверка миграции Liquibase

**Файл:** [`src/main/resources/db/changelog/changes/003-add-product-status-history.yaml`](src/main/resources/db/changelog/changes/003-add-product-status-history.yaml)

**Статус:** Миграция уже существует. Проверяем, что она включена в master changelog.

**Файл:** [`src/main/resources/db/changelog/db.changelog-master.yaml`](src/main/resources/db/changelog/db.changelog-master.yaml)

**Действие:** Убедиться, что файл `003-add-product-status-history.yaml` включён в master changelog.

---

### Шаг 9: Обновление OpenAPI документации

**Файл:** [`src/main/resources/openapi.yaml`](src/main/resources/openapi.yaml)

**Действие:** Добавить новый endpoint в OpenAPI спецификацию.

---

### Шаг 10: Написание тестов

**Файл:** [`src/test/kotlin/ru/example/product/data/controller/StatusHistoryControllerTest.kt`](src/test/kotlin/ru/example/product/data/controller/StatusHistoryControllerTest.kt)

**Новый файл.** Тесты контроллера.

**Файл:** [`src/test/kotlin/ru/example/product/data/service/ProductStatusHistoryServiceTest.kt`](src/test/kotlin/ru/example/product/data/service/ProductStatusHistoryServiceTest.kt)

**Новый файл.** Тесты сервиса.

---

### Шаг 11: Обновление AGENTS.md

**Файл:** [`AGENTS.md`](AGENTS.md)

**Действие:** Добавить новую сущность, DTO, сервис и API endpoint в документацию.

---

## 6. Структура новых файлов

```
src/main/kotlin/ru/example/product/data/
├── controller/
│   └── StatusHistoryController.kt          # NEW
├── domain/
│   ├── ProductStatusHistoryEntity.kt       # NEW
│   └── ProductStatusHistoryDto.kt          # NEW
├── dto/
│   └── request/
│       └── StatusHistoryQueryRequest.kt    # NEW
├── mappers/
│   └── ProductStatusHistoryMapper.kt       # NEW
├── repository/
│   └── ProductStatusHistoryRepository.kt   # NEW
└── service/
    ├── ProductStatusHistoryService.kt      # NEW
    └── ProductStatusHistoryServiceImpl.kt  # NEW

src/test/kotlin/ru/example/product/data/
├── controller/
│   └── StatusHistoryControllerTest.kt      # NEW
└── service/
    └── ProductStatusHistoryServiceTest.kt  # NEW
```

---

## 7. Взаимосвязь с фронтендом

```mermaid
sequenceDiagram
    participant FE as Frontend (useRealtimeData)
    participant BE as StatusHistoryController
    participant Svc as ProductStatusHistoryService
    participant Repo as ProductStatusHistoryRepository
    participant DB as PostgreSQL

    Note over FE: Первичная загрузка
    FE->>BE: GET /api/v1/status-history?limit=1000
    BE->>Svc: getStatusHistory(query)
    Svc->>Repo: findTopByOrderByCreatedAtDesc(1000)
    Repo->>DB: SELECT * FROM product_status_history ORDER BY created_at DESC LIMIT 1000
    DB-->>Repo: List<ProductStatusHistoryEntity>
    Repo-->>Svc: List<Entity>
    Svc-->>BE: List<ProductStatusHistoryDto>
    BE-->>FE: ApiResponse<List<ProductStatusHistoryDto>>

    Note over FE: Поллинг (каждую секунду)
    loop Каждую секунду
        FE->>BE: GET /api/v1/status-history?created_after={lastTimestamp}&limit=100
        BE->>Svc: getStatusHistory(query)
        Svc->>Repo: findByCreatedAtAfterOrderByCreatedAtDesc(createdAfter, 100)
        Repo->>DB: SELECT * FROM product_status_history WHERE created_at > ? ORDER BY created_at DESC LIMIT 100
        DB-->>Repo: List<Entity>
        Repo-->>Svc: List<Entity>
        Svc-->>BE: List<ProductStatusHistoryDto>
        BE-->>FE: ApiResponse<List<ProductStatusHistoryDto>>
    end
```

---

## 8. Критерии готовности

- [ ] Все новые файлы созданы в правильных пакетах
- [ ] Entity маппится на таблицу `product.product_status_history`
- [ ] DTO сериализуется в JSON с правильными полями (camelCase)
- [ ] Endpoint `GET /api/v1/status-history` работает с query params
- [ ] Фильтрация по `createdAfter`, `productId`, `toStatus` работает
- [ ] По умолчанию `limit=1000`, сортировка по `created_at DESC`
- [ ] Миграция `003` включена в master changelog
- [ ] OpenAPI документация обновлена
- [ ] Тесты проходят (`./gradlew test`)
- [ ] Приложение собирается (`./gradlew build`)
- [ ] Swagger UI показывает новый endpoint

---

## 9. Чеклист при реализации

- [ ] Код соответствует стилю ktlint
- [ ] Добавлены валидационные аннотации в DTO
- [ ] Добавлены аннотации `@Schema` для OpenAPI
- [ ] Добавлено логирование (INFO/DEBUG)
- [ ] Обработаны edge cases (null values, empty results)
- [ ] UUID конвертируются в строку и обратно корректно
- [ ] Timestamp парсится из ISO-8601 строки
- [ ] Написаны unit тесты
- [ ] Нет утечек ресурсов

---

## 10. Обработка ошибок

| Ситуация | HTTP Status | Сообщение |
|----------|-------------|-----------|
| Invalid `createdAfter` format | 400 | "Invalid createdAfter format: ..." |
| `limit` < 1 или > 10000 | 400 | "limit must be between 1 and 10000" |
| Invalid `productId` UUID format | 400 | "Invalid productId format: ..." |
| Пустой результат | 200 | `{"data": []}` — пустой массив, не ошибка |
| База данных недоступна | 500 | "Internal server error" |

---

## 11. Пошаговый план выполнения

| Шаг | Действие | Файлы |
|-----|----------|-------|
| 1 | Создать ProductStatusHistoryEntity | `src/main/kotlin/.../domain/ProductStatusHistoryEntity.kt` |
| 2 | Создать ProductStatusHistoryDto | `src/main/kotlin/.../domain/ProductStatusHistoryDto.kt` |
| 3 | Создать StatusHistoryQueryRequest | `src/main/kotlin/.../dto/request/StatusHistoryQueryRequest.kt` |
| 4 | Создать ProductStatusHistoryRepository | `src/main/kotlin/.../repository/ProductStatusHistoryRepository.kt` |
| 5 | Создать ProductStatusHistoryMapper | `src/main/kotlin/.../mappers/ProductStatusHistoryMapper.kt` |
| 6 | Создать ProductStatusHistoryService интерфейс | `src/main/kotlin/.../service/ProductStatusHistoryService.kt` |
| 7 | Создать ProductStatusHistoryServiceImpl | `src/main/kotlin/.../service/ProductStatusHistoryServiceImpl.kt` |
| 8 | Создать StatusHistoryController | `src/main/kotlin/.../controller/StatusHistoryController.kt` |
| 9 | Проверить миграцию 003 в master changelog | `src/main/resources/db/changelog/db.changelog-master.yaml` |
| 10 | Написать тесты контроллера | `src/test/kotlin/.../controller/StatusHistoryControllerTest.kt` |
| 11 | Написать тесты сервиса | `src/test/kotlin/.../service/ProductStatusHistoryServiceTest.kt` |
| 12 | Обновить OpenAPI документацию | `src/main/resources/openapi.yaml` |
| 13 | Обновить AGENTS.md | `AGENTS.md` |
| 14 | Запустить сборку и тесты | `./gradlew build` |
