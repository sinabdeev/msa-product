# AI Agent Guidelines - MSA Product Processing

## Project Overview

**Name:** MSA Product Processing (msa-product-processing)
**Type:** Spring Boot microservice for product processing
**Language:** Kotlin 1.9.25
**Framework:** Spring Boot 3.5.13
**Database:** PostgreSQL with Spring Data JDBC
**Build Tool:** Gradle 8+ (Kotlin DSL)

## Code Style Guidelines

### Kotlin Conventions

- Use Kotlin idioms: data classes, sealed classes, when expressions, extension functions
- Prefer immutable properties (`val` over `var`)
- Use null-safety features (`?`, `!!`, `?:`, `let`, `apply`, `run`, `also`)
- Use string templates instead of string concatenation
- Prefer single-expression functions when appropriate
- Use named arguments for functions with multiple parameters
- Use `object` for singletons, `companion object` for static-like members

### Naming Conventions

- Classes: PascalCase (`ProductEntity`, `ProductStatusTransitionValidator`)
- Interfaces: PascalCase (`ProductStatusTransitionService`)
- Implementations: PascalCase with `Impl` suffix (`ProductStatusTransitionValidatorImpl`)
- Functions: camelCase (`updateStatus`, `getPossibleTransitions`)
- Constants: UPPER_SNAKE_CASE in companion objects or object singletons
- Packages: lowercase with dot separators (`ru.example.product.processing.domain`)

### File Organization

```
src/main/kotlin/ru/example/product/processing/
├── MsaProductProcessingApplication.kt    # Main application entry point
├── config/                                # Spring configurations
│   ├── converter/                         # Type converters
│   └── *.kt                              # Other configs (JdbcConfig, ThreadPoolConfig)
├── domain/                                # Domain models
│   ├── *.kt                              # Entity classes
│   └── status/                           # Status-related domain objects
├── repository/                            # Data access layer
├── service/                               # Business logic
│   ├── status/                            # Status-related services
│   └── history/                           # History-related services
└── exception/                             # Custom exceptions
```

### Domain Layer Rules

- Domain entities extend nothing (no JPA annotations)
- Use `@Entity` from Spring Data JDBC
- IDs should be UUID type
- Use `Instant` for timestamps (`createdAt`, `updatedAt`)
- Status enums should include code values for database mapping
- Use sealed classes for status states when appropriate

### Service Layer Rules

- Services should be interfaces + implementations pattern
- Interface: `ProductStatusTransitionService.kt`
- Implementation: `ProductStatusTransitionServiceImpl.kt`
- Use `@Service` annotation on implementations
- Inject dependencies via constructor injection
- Use `@Async` for asynchronous operations
- Use `@Scheduled` for periodic tasks with configuration properties

### Configuration Rules

- Use `application.yaml` for externalized configuration
- Custom properties under `product.*` namespace
- Use `@ConfigurationProperties` for type-safe configuration
- Database credentials via environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`)

### Testing Guidelines

- Test files in `src/test/kotlin/ru/example/product/processing/`
- Mirror main source structure
- Use `@SpringBootTest` for integration tests
- Use `@ExtendWith(MockitoExtension::class)` for unit tests
- Test class naming: `ClassNameTest.kt`
- Use AAA pattern: Arrange, Act, Assert

## Domain Model Reference

### ProductEntity

| Field         | Type         | Description           |
|---------------|--------------|-----------------------|
| `id`          | UUID         | Unique identifier     |
| `sku`         | String       | Product article/SKU   |
| `name`        | String       | Product name          |
| `description` | String       | Product description   |
| `price`       | BigDecimal   | Price                 |
| `quantity`    | Int          | Quantity              |
| `weight`      | Double?      | Weight (nullable)     |
| `isAvailable` | Boolean      | Availability flag     |
| `status`      | ProductStatus| Current status        |
| `category`    | ProductCategory| Product category    |
| `tags`        | List<String> | JSONB tags array      |
| `createdAt`   | Instant      | Creation timestamp    |
| `updatedAt`   | Instant      | Last update timestamp |

### ProductStatus Enum

| Status         | Code | Description                    |
|----------------|------|--------------------------------|
| DRAFT          | 0    | Черновик продукта              |
| PENDING_REVIEW | 10   | Ожидает проверки               |
| REVIEWED       | 20   | Проверено модератором          |
| APPROVED       | 30   | Утверждено менеджером          |
| REJECTED       | 40   | Отклонено                      |
| ACTIVE         | 50   | Активно для продажи            |
| PROCESSED      | 60   | Обработано (заказ собран)      |
| SHIPPED        | 70   | Отправлено клиенту             |
| ARCHIVED       | 80   | Архивировано (завершено)       |

### Status Transitions

Valid transition paths:
1. **Success workflow:** `DRAFT → PENDING_REVIEW → REVIEWED → APPROVED → ACTIVE → PROCESSED → SHIPPED → ARCHIVED`
2. **Rejection with correction:** `DRAFT → PENDING_REVIEW → REVIEWED → REJECTED → PENDING_REVIEW → ...`
3. **Early archiving:** `DRAFT → PENDING_REVIEW → ARCHIVED`
4. **Archive restoration:** `ARCHIVED → ACTIVE → PROCESSED → SHIPPED → ARCHIVED`

## API Reference

- Swagger UI: `http://localhost:8082/swagger-ui.html`
- OpenAPI: `http://localhost:8082/api-docs`
- Port: 8082

## Build Commands

```bash
./gradlew build          # Build project
./gradlew bootRun        # Run application
./gradlew test           # Run tests
./gradlew ktlintCheck    # Code style check
./gradlew ktlintFormat   # Auto-format code
```

## Important Notes

- All new features should include unit tests and integration tests
- Status transitions must be validated before execution
- History records must be created for every status change
- Thread pool configuration: core=4, max=4, queue=256, keep-alive=60s
- Scheduled tasks must use `product.scheduling.enabled` condition
- Never modify status directly in database - always use `ProductStatusTransitionService`
