# MSA Product Generator

Микросервис для генерации реалистичных продуктовых данных с автоматической отправкой в REST API по расписанию.

## 📋 О проекте

Этот микросервис решает задачу автоматической генерации реалистичных продуктовых данных и их отправки в указанный REST API. Сервис работает по расписанию, генерируя 10 продуктов каждую минуту и отправляя их асинхронно в два потока.

### Основные возможности

- **Генерация реалистичных продуктов**: Создание продуктов с правдоподобными названиями, описаниями, ценами и характеристиками
- **Работа по расписанию**: Автоматический запуск каждые 60 секунд
- **Асинхронная обработка**: Параллельная генерация и отправка данных в два потока
- **Словари данных**: Использование обширных словарей (по 50+ позиций в каждом) для реалистичности
- **REST API интеграция**: Отправка сгенерированных данных в указанный эндпоинт
- **Поддержка базы данных**: Сохранение продуктов в PostgreSQL с использованием Liquibase для миграций
- **Документация API**: Полная OpenAPI документация через Swagger UI

## 🏗️ Архитектура

Проект построен на основе Spring Boot 3.5.13 с использованием Kotlin и следующих технологий:

- **Spring Boot 3.5.13** - основной фреймворк
- **Kotlin 1.9.25** - язык программирования
- **Spring Data JDBC** - работа с базой данных
- **PostgreSQL** - основная СУБД
- **Liquibase** - управление миграциями базы данных
- **Spring Scheduler** - планировщик задач
- **Spring Async** - асинхронное выполнение
- **SpringDoc OpenAPI** - документация API
- **Ktlint** - линтер для Kotlin

## 📁 Структура проекта

```
msa-product-generator/
├── src/main/kotlin/ru/example/product/generator/
│   ├── MsaProductGeneratorApplication.kt      # Точка входа приложения
│   ├── config/                                # Конфигурационные классы
│   │   ├── ThreadPoolConfig.kt               # Конфигурация пулов потоков
│   │   ├── JdbcConfig.kt                     # Конфигурация JDBC
│   │   ├── OpenApiConfig.kt                  # Конфигурация OpenAPI
│   │   └── converter/JsonbConverters.kt      # Конвертеры JSONB
│   ├── controller/                           # REST контроллеры
│   │   ├── ProductController.kt              # Основной контроллер продуктов
│   │   └── exception/GlobalExceptionHandler.kt # Обработчик исключений
│   ├── domain/                               # Доменные модели
│   │   ├── ProductEntity.kt                  # Сущность продукта
│   │   ├── ProductDto.kt                     # DTO продукта
│   │   └── ProductCategory.kt                # Перечисление категорий
│   ├── dto/                                  # DTO объекты
│   │   ├── request/                          # DTO запросов
│   │   ├── response/                         # DTO ответов
│   │   └── error/ApiError.kt                 # DTO ошибок
│   ├── generator/                            # Логика генерации продуктов
│   │   ├── ProductGeneratorService.kt        # Сервис генерации
│   │   └── ProductDictionary.kt              # Словари данных
│   ├── scheduler/                            # Планировщики
│   │   └── ProductScheduler.kt               # Планировщик генерации
│   ├── service/                              # Бизнес-логика
│   │   ├── ProductService.kt                 # Интерфейс сервиса
│   │   ├── ProductServiceImpl.kt             # Реализация сервиса
│   │   └── ProductHandler.kt                 # Обработчик продуктов
│   ├── repository/                           # Репозитории
│   │   └── ProductRepository.kt              # Репозиторий продуктов
│   ├── mappers/                              # Мапперы
│   │   └── ProductMapper.kt                  # Маппер сущностей
│   ├── client/                               # Клиенты внешних сервисов
│   │   └── ProductBatchClient.kt             # Клиент для отправки батчей
│   └── exception/                            # Исключения
│       └── ProductNotFoundException.kt       # Исключение "Продукт не найден"
├── src/main/resources/
│   ├── application.yaml                      # Основная конфигурация
│   ├── openapi.yaml                          # Спецификация OpenAPI
│   ├── db/changelog/                         # Миграции Liquibase
│   └── Product API.postman_collection.json   # Коллекция Postman
└── src/test/                                 # Тесты
```

## 🚀 Быстрый старт

### Предварительные требования

- **Java 17** или выше
- **PostgreSQL 12+** (или Docker для запуска контейнера)
- **Gradle 8+** (используется wrapper)

### Установка и запуск

1. **Клонирование репозитория**
   ```bash
   git clone <repository-url>
   cd msa-product-generator
   ```

2. **Настройка базы данных**
   ```bash
   # Создание базы данных в PostgreSQL
   createdb product_db
   
   # Или использование Docker
   docker run --name product-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_DB=product_db -p 5432:5432 -d postgres:15
   ```

3. **Настройка переменных окружения**
   ```bash
   # Linux/Mac
   export DB_URL=jdbc:postgresql://localhost:5432/product_db
   export DB_USER=postgres
   export DB_PASSWORD=password
   
   # Windows (CMD)
   set DB_URL=jdbc:postgresql://localhost:5432/product_db
   set DB_USER=postgres
   set DB_PASSWORD=password
   ```

4. **Запуск приложения**
   ```bash
   # Используя Gradle wrapper
   ./gradlew bootRun
   
   # Или собранный JAR
   ./gradlew build
   java -jar build/libs/msa-product-generator-0.0.1.jar
   ```

5. **Проверка работы**
   - Приложение будет доступно по адресу: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - API Docs: `http://localhost:8080/api-docs`

## ⚙️ Конфигурация

Основные настройки в `application.yaml`:

```yaml
# База данных
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/product_db}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:password}

# Планировщик
product:
  generator:
    thread-pool:
      core-size: 2      # Количество потоков для генерации
      max-size: 2       # Максимальное количество потоков
```

### Переменные окружения

| Переменная | Описание | Значение по умолчанию |
|------------|----------|----------------------|
| `DB_URL` | URL базы данных PostgreSQL | `jdbc:postgresql://localhost:5432/product_db` |
| `DB_USER` | Имя пользователя БД | `postgres` |
| `DB_PASSWORD` | Пароль пользователя БД | `password` |
| `SERVER_PORT` | Порт приложения | `8080` |

## 📊 Генерация продуктов

### Алгоритм генерации

1. **Выбор категории**: Случайный выбор из 6 категорий (ELECTRONICS, CLOTHING, FOOD, HOME_GOODS, APPLIANCES, SMART_HOME)
2. **Генерация атрибутов**: Комбинирование данных из словарей:
   - Бренды (50+ позиций)
   - Типы продуктов (50+ позиций)
   - Прилагательные (50+ позиций)
   - Цвета (50+ позиций)
   - Материалы (50+ позиций)
   - Теги (50+ позиций)
3. **Формирование SKU**: Уникальный код формата `BRAND-PRODUCT-COLOR-RANDOM`
4. **Генерация названия**: Комбинация прилагательного, типа продукта, цвета, бренда и суффикса
5. **Расчет цены**: Диапазон цен зависит от категории
6. **Определение веса**: Зависит от типа продукта
7. **Генерация описания**: Автоматическое создание на основе характеристик

### Пример сгенерированного продукта

```json
{
  "sku": "SON-HEA-BLA-4827",
  "name": "Премиум Наушники Black Sony WH-1000XM5",
  "description": "Высококачественные наушники Black изготовлены из прочного пластика. Идеально подходят для повседневного использования с поддержкой Bluetooth 5.3 и активным шумоподавлением.",
  "price": 34990.00,
  "quantity": 15,
  "isAvailable": true,
  "category": "ELECTRONICS",
  "weight": 0.25,
  "tags": ["Bluetooth", "Шумоподавление", "Hi-Res Audio", "Беспроводные", "Складные"]
}
```

## 🔧 API Endpoints

### Основные эндпоинты

| Метод | Путь | Описание |
|-------|------|----------|
| `GET` | `/api/v1/products` | Получить список всех продуктов |
| `GET` | `/api/v1/products/{id}` | Получить продукт по ID |
| `POST` | `/api/v1/products` | Создать новый продукт |
| `POST` | `/api/v1/products/batch` | Создать несколько продуктов (батч) |
| `PUT` | `/api/v1/products/{id}` | Обновить продукт |
| `DELETE` | `/api/v1/products/{id}` | Удалить продукт |

### Пример запроса создания продукта

```bash
curl -X POST "http://localhost:8080/api/v1/products" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "AUD-WH-1000XM5-B",
    "name": "Беспроводные наушники Sony WH-1000XM5 Black",
    "description": "Полноразмерные наушники с лучшим в отрасли шумоподавлением, 30 часами работы и поддержкой Hi-Res Audio.",
    "price": 34990.00,
    "quantity": 15,
    "isAvailable": true,
    "category": "ELECTRONICS",
    "weight": 0.25,
    "tags": ["Bluetooth", "Шумоподавление"]
  }'
```

## ⏰ Планировщик задач

### Конфигурация планировщика

Планировщик `ProductScheduler` настроен на выполнение каждые 60 секунд:

```kotlin
@Scheduled(fixedDelay = 60000, initialDelay = 5000)
@Async("productGeneratorThreadPool")
fun generateAndSubmitProducts() {
    // Генерация 10 продуктов и отправка в API
}
```

### Параметры выполнения

- **Интервал**: 60 секунд между выполнениями
- **Количество продуктов**: 10 за один запуск
- **Потоки**: 2 параллельных потока для асинхронной обработки
- **Задержка при старте**: 5 секунд для инициализации приложения

## 🧪 Тестирование

### Запуск тестов

```bash
# Все тесты
./gradlew test

# С покрытием кода
./gradlew test jacocoTestReport

# Конкретный тестовый класс
./gradlew test --tests "*ProductGeneratorServiceTest"
```

### Типы тестов

1. **Unit-тесты**: Тестирование отдельных компонентов
   - `ProductGeneratorServiceTest` - тесты генерации продуктов
   - `ProductDictionaryTest` - тесты словарей данных

2. **Интеграционные тесты**: Тестирование взаимодействия компонентов
   - `ProductControllerTest` - тесты REST контроллера
   - `ProductServiceParallelTest` - тесты параллельной обработки

3. **Тесты приложения**: Общее тестирование приложения
   - `MsaProductGeneratorApplicationTests` - контекстные тесты

## 🐳 Docker

### Сборка Docker образа

```bash
# Сборка JAR
./gradlew build

# Сборка Docker образа
docker build -t msa-product-generator:latest .
```

### Docker Compose

Пример `docker-compose.yml` для полного развертывания:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: product_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    image: msa-product-generator:latest
    environment:
      DB_URL: jdbc:postgresql://postgres:5432/product_db
      DB_USER: postgres
      DB_PASSWORD: password
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

Запуск:
```bash
docker-compose up -d
```

## 📈 Мониторинг и логирование

### Уровни логирования

Конфигурация логирования в `application.yaml`:

```yaml
logging:
  level:
    ru.example.product.generator: DEBUG
    org.springframework: WARN
    org.springframework.web: DEBUG
```

### Ключевые логи

- **Генерация продуктов**: `ProductGeneratorService` - логирует процесс генерации
- **Планировщик**: `ProductScheduler` - логирует выполнение по расписанию
- **API вызовы**: `ProductBatchClient` - логирует отправку данных
- **Ошибки**: Все компоненты логируют исключения с полным стектрейсом

## 🔄 Миграции базы данных

### Использование Liquibase

Миграции находятся в `src/main/resources/db/changelog/`:

- `db.changelog-master.yaml` - главный файл миграций
- `changes/001-initial-schema.yaml` - начальная схема

### Создание новой миграции

```bash
# Генерация SQL из существующей БД (опционально)
./gradlew liquibaseDiffChangelog

# Применение миграций при запуске
./gradlew bootRun
```

## 🛠️ Разработка

### Стиль кода

Проект использует Ktlint для обеспечения единого стиля кода:

```bash
# Проверка стиля
./gradlew ktlintCheck

# Автоматическое исправление
./gradlew ktlintFormat
```

### Структура коммитов

Рекомендуется использовать Conventional Commits:

- `feat:` Новая функциональность
- `fix:` Исправление ошибки
- `docs:` Изменения в документации
- `style:` Форматирование, отсутствующие точки с запятой и т.д.
- `refactor:` Рефакторинг кода
- `test:` Добавление или исправление тестов
- `chore:` Изменения в процессе сборки или вспомогательные инструменты

### Ветвление

- `master` - основная ветка, всегда стабильна
- `develop` - ветка разработки
- `feature/*` - ветки для новых функций
- `bugfix/*` - ветки для исправления ошибок

## 🤝 Вклад в проект

1. Форкните репозиторий
2. Создайте ветку для вашей функции (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'feat: add amazing feature'`)
4. Отправьте в ветку (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

---

**Примечание**: Этот микросервис предназначен для демонстрационных и тестовых целей. В production среде рекомендуется дополнительная настройка безопасности, мониторинга и отказоустойчивости.
