# MSA Product Receiver

Микросервис для управления продуктами, построенный на Spring Boot с использованием Kotlin, PostgreSQL и OpenAPI.

## Оглавление

- [Обзор](#обзор)
- [Функциональности](#функциональности)
- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура)
- [Требования](#требования)
- [Установка и запуск](#установка-и-запуск)
- [Конфигурация](#конфигурация)
- [API Документация](#api-документация)
- [Тестирование](#тестирование)
- [Параллельная обработка](#параллельная-обработка)
- [База данных](#база-данных)
- [Логирование](#логирование)
- [Структура проекта](#структура-проекта)
- [Планы развития](#планы-развития)
- [Лицензия](#лицензия)

## Обзор

MSA Product Receiver — это микросервис, предоставляющий REST API для управления продуктами (CRUD операции) с поддержкой пакетного создания и параллельной обработки. Сервис разработан с использованием современных практик: асинхронность, валидация, стандартизированные ответы и полная документация OpenAPI.

## Функциональности

- **CRUD операций над продуктами**: создание, чтение, обновление, удаление
- **Пакетное создание продуктов**: массовая загрузка с параллельной обработкой
- **Валидация данных**: аннотации Jakarta Validation
- **Стандартизированные ответы API**: единый формат ответов и ошибок
- **Автоматическая документация OpenAPI 3.0**: Swagger UI
- **Асинхронная обработка**: использование `@Async` и настраиваемого пула потоков
- **Миграции базы данных**: Liquibase для управления схемой
- **Поддержка JSONB в PostgreSQL**: хранение дополнительных атрибутов
- **Логирование с MDC**: трассировка запросов через correlationId
- **Интеграционные и unit-тесты**: полное покрытие

## Технологический стек

- **Язык**: Kotlin 1.9.25
- **Фреймворк**: Spring Boot 3.5.13
- **База данных**: PostgreSQL 15+ (с поддержкой JSONB)
- **Миграции**: Liquibase
- **Документация API**: SpringDoc OpenAPI 2.5.0
- **Валидация**: Jakarta Bean Validation 3.0
- **Логирование**: Logback с MDC
- **Тестирование**: JUnit 5, Spring Boot Test, H2
- **Сборка**: Gradle (Kotlin DSL) с ktlint
- **Контейнеризация**: Docker (опционально)

## Архитектура

Сервис следует многослойной архитектуре:

1. **Controller слой** (`ProductController`) — обработка HTTP запросов, валидация, маппинг DTO
2. **Service слой** (`ProductService`, `ProductHandler`) — бизнес-логика, параллельная обработка
3. **Repository слой** (`ProductRepository`) — доступ к данным через Spring Data JDBC
4. **Domain слой** (`ProductEntity`, `ProductDto`) — сущности и DTO
5. **Configuration** — настройки пула потоков, OpenAPI, конвертеры JSONB

Для пакетных операций используется асинхронный обработчик (`ProductHandler`), который распределяет задачи по потокам из настраиваемого пула (`ThreadPoolConfig`).

## Требования

- Java 17 или выше
- PostgreSQL 15+
- Gradle 8+ (используется wrapper)
- (Опционально) Docker и Docker Compose

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone <repository-url>
cd msa-product-receiver
```

### 2. Настройка базы данных

Создайте базу данных в PostgreSQL:

```sql
CREATE DATABASE product_db;
CREATE USER product_user WITH PASSWORD 'product_pass';
GRANT ALL PRIVILEGES ON DATABASE product_db TO product_user;
```

### 3. Конфигурация

Отредактируйте `src/main/resources/application.yaml` при необходимости:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/product_db
    username: product_user
    password: product_pass
```

### 4. Запуск приложения

Используйте Gradle wrapper:

```bash
./gradlew bootRun
```

Или соберите и запустите JAR:

```bash
./gradlew build
java -jar build/libs/msa-product-receiver-0.0.1.jar
```

Приложение будет доступно по адресу: `http://localhost:8080`

## Конфигурация

Основные настройки в `application.yaml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.yaml
    enabled: true
    default-schema: product
  jackson:
    serialization:
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

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

Настройки пула потоков для параллельной обработки находятся в `ThreadPoolConfig`.

## API Документация

После запуска сервиса документация OpenAPI доступна по адресам:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Основные эндпоинты

| Метод | Путь | Описание |
|-------|------|----------|
| POST | `/api/v1/products` | Создание одного продукта |
| POST | `/api/v1/products/batch` | Пакетное создание продуктов |
| GET | `/api/v1/products` | Получение списка продуктов (пагинация) |
| GET | `/api/v1/products/{id}` | Получение продукта по ID |
| PUT | `/api/v1/products/{id}` | Обновление продукта |
| DELETE | `/api/v1/products/{id}` | Удаление продукта |

### Пример запроса создания продукта

```bash
curl -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "SKU-12345",
    "name": "Example Product",
    "description": "A test product",
    "price": 99.99,
    "category": "ELECTRONICS",
    "stockQuantity": 100,
    "attributes": {"color": "black", "weight": "1.5kg"}
  }'
```

## Тестирование

Запуск тестов:

```bash
./gradlew test
```

Тесты используют H2 in-memory базу данных. Конфигурация тестов находится в `src/test/resources/application-test.yaml`.

### Типы тестов

- **Unit-тесты**: сервисы, мапперы, валидация
- **Интеграционные тесты**: контроллеры с `@SpringBootTest`
- **Параллельные тесты**: проверка многопоточной обработки (`ProductServiceParallelTest`)

## Параллельная обработка

Сервис поддерживает пакетное создание продуктов с параллельной обработкой через `ProductHandler`. Конфигурация пула потоков (`ThreadPoolConfig`) позволяет настроить:

- `product.batch.thread-pool.core-size`: количество основных потоков
- `product.batch.thread-pool.max-size`: максимальное количество потоков
- `product.batch.thread-pool.queue-capacity`: ёмкость очереди задач
- `product.batch.thread-pool.keep-alive-seconds`: время жизни неиспользуемых потоков
- `product.batch.thread-pool.thread-name-prefix`: префикс имён потоков

Пример пакетного запроса:

```bash
curl -X POST "http://localhost:8080/api/v1/products/batch" \
  -H "Content-Type: application/json" \
  -d '{
    "products": [
      { "sku": "SKU-1", "name": "Product 1", "price": 10.0 },
      { "sku": "SKU-2", "name": "Product 2", "price": 20.0 }
    ]
  }'
```

Ответ содержит результаты по каждому продукту и общую статистику.

## База данных

### Схема

Основная таблица `products`:

| Колонка | Тип | Описание |
|---------|-----|----------|
| id | UUID | Первичный ключ |
| sku | VARCHAR(255) UNIQUE | Артикул (уникальный) |
| name | VARCHAR(255) | Название |
| description | TEXT | Описание |
| price | DECIMAL(10,2) | Цена |
| category | VARCHAR(50) | Категория (enum) |
| stock_quantity | INTEGER | Количество на складе |
| attributes | JSONB | Дополнительные атрибуты |
| created_at | TIMESTAMP | Дата создания |
| updated_at | TIMESTAMP | Дата обновления |

### Миграции

Управление схемой через Liquibase. Файлы миграций находятся в `src/main/resources/db/changelog/`.

## Логирование

Настроено логирование с использованием MDC (Mapped Diagnostic Context) для трассировки запросов через `correlationId`. Конфигурация Logback в `src/main/resources/logback-spring.xml`.

Уровни логирования настраиваются через `application.yaml`:

```yaml
logging:
  level:
    ru.example.product.receiver: DEBUG
```

## Структура проекта

```
msa-product-receiver/
├── src/main/kotlin/ru/example/product/receiver/
│   ├── config/              # Конфигурационные классы
│   ├── controller/          # REST контроллеры
│   ├── domain/              # Сущности и DTO
│   ├── dto/                 # Запросы и ответы
│   ├── exception/           # Кастомные исключения
│   ├── mappers/             # Мапперы (MapStruct)
│   ├── repository/          # Репозитории (Spring Data JDBC)
│   └── service/             # Бизнес-логика
├── src/main/resources/
│   ├── db/changelog/        # Миграции Liquibase
│   ├── application.yaml     # Основная конфигурация
│   └── openapi.yaml         # Спецификация OpenAPI
├── src/test/kotlin/         # Тесты
├── plans/                   # Планы развития и документация
├── build.gradle.kts         # Скрипт сборки
└── README.md                # Этот файл
```

## Планы развития

В папке `plans/` находятся документы по дальнейшему развитию:

- `product-api-implementation-plan.md` — план реализации API
- `parallel-saving-implementation-todo.md` — задачи по параллельному сохранению
- `logging-implementation-plan.md` — улучшение логирования
- `jsonb-fix-implementation-plan.md` — исправления работы с JSONB
- `ktlint-implementation-plan.md` — настройка линтинга

