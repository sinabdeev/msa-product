# Product API Implementation Plan

## Overview
This document outlines the implementation plan for adding Product DTO, REST API, and OpenAPI/Swagger documentation to the msa-product-receiver Spring Boot application.

## Requirements Analysis
From TASK-01:
1. Create Product DTO with specified fields
2. Implement REST API methods for creating, updating, and deleting products
3. Create OpenAPI YAML file
4. Add Swagger annotations for API documentation
5. Add Swagger UI

## Architecture Decisions
- **Database Layer**: Spring Data JDBC (already configured) with PostgreSQL
- **API Base Path**: `/api/v1/products`
- **OpenAPI Generation**: SpringDoc OpenAPI with annotations
- **OpenAPI YAML Location**: `src/main/resources/openapi.yaml`
- **Validation**: Jakarta Bean Validation
- **Error Handling**: Global `@ControllerAdvice` with standardized error responses

## Database Schema Design

### Products Table
```sql
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19,4) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    weight DOUBLE PRECISION,
    is_available BOOLEAN NOT NULL DEFAULT true,
    category VARCHAR(50) NOT NULL,
    tags JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

### Notes
- `tags` stored as JSONB column for simplicity (PostgreSQL JSONB)
- `category` stored as VARCHAR mapping to enum
- Timestamps use `Instant` in Kotlin, `TIMESTAMPTZ` in DB

## Database Migrations with Liquibase

The project uses **Liquibase** for database schema versioning and migrations.

### Configuration
- **Dependency**: `org.liquibase:liquibase-core` added to `build.gradle.kts`
- **Configuration**: In `application.yaml`:
  ```yaml
  spring:
    liquibase:
      change-log: classpath:db/changelog/db.changelog-master.yaml
      enabled: true
  ```

### Migration Structure
- **Master changelog**: `src/main/resources/db/changelog/db.changelog-master.yaml`
- **Migration files**: Stored in `src/main/resources/db/changelog/changes/`
- **Initial migration**: `001-initial-schema.yaml` creates the `products` table with all required columns and indexes

### How It Works
1. On application startup, Liquibase checks the database changelog table (`databasechangelog`)
2. Any unapplied changesets are executed in order
3. The schema is automatically kept in sync with the codebase
4. New migrations can be added as separate YAML files and included in the master changelog

### Creating New Migrations
1. Create a new YAML file in `db/changelog/changes/` (e.g., `002-add-new-column.yaml`)
2. Add the changeset with a unique ID and author
3. Include the new file in `db.changelog-master.yaml`

## Class Structure

### 1. Domain Model
- `ProductCategory` enum (ELECTRONICS, CLOTHING, FOOD, HOME_GOODS)
- `ProductEntity` - database entity with Spring Data JDBC annotations
- `ProductDto` - API response DTO (as specified in TASK-01)

### 2. Request/Response DTOs
- `CreateProductRequest` - for POST endpoint (validation annotations)
- `UpdateProductRequest` - for PUT endpoint
- `ApiResponse<T>` - standardized response wrapper

### 3. Repository Layer
- `ProductRepository` interface extending `CrudRepository<ProductEntity, UUID>`

### 4. Service Layer
- `ProductService` - business logic, mapping between entity/DTO
- `ProductServiceImpl` - implementation

### 5. Controller Layer
- `ProductController` - REST controller with CRUD operations

### 6. Error Handling
- `ApiError` - error response structure
- `GlobalExceptionHandler` - `@ControllerAdvice` class

## API Endpoints

| Method | Path | Description | Status Codes |
|--------|------|-------------|--------------|
| POST | `/api/v1/products` | Create new product | 201 Created, 400 Bad Request |
| GET | `/api/v1/products` | List all products (with pagination) | 200 OK |
| GET | `/api/v1/products/{id}` | Get product by ID | 200 OK, 404 Not Found |
| PUT | `/api/v1/products/{id}` | Update product | 200 OK, 404 Not Found |
| DELETE | `/api/v1/products/{id}` | Delete product | 204 No Content, 404 Not Found |

## Implementation Steps

### Phase 1: Foundation
1. **Update Dependencies** (`build.gradle.kts`)
   - Add `springdoc-openapi-starter-webmvc-ui`
   - Ensure validation dependencies are present

2. **Create Domain Classes**
   - `ProductCategory.kt` enum
   - `ProductEntity.kt` with JDBC annotations
   - `ProductDto.kt` (as per TASK-01 specification)

3. **Create Request/Response DTOs**
   - `CreateProductRequest.kt`
   - `UpdateProductRequest.kt`
   - `ApiResponse.kt`

### Phase 2: Data Layer
4. **Create Repository**
   - `ProductRepository.kt` interface

5. **Create Service Layer**
   - `ProductService.kt` interface
   - `ProductServiceImpl.kt` implementation

6. **Database Configuration**
   - Consider adding `@ConfigurationProperties` for database settings
   - Ensure JSONB support for tags field

### Phase 3: API Layer
7. **Create Controller**
   - `ProductController.kt` with CRUD methods
   - Add `@RestController` and `@RequestMapping("/api/v1/products")`

8. **Add Validation**
   - Annotate request DTOs with `@NotNull`, `@Size`, `@Min`, `@Max`
   - Add `@Valid` annotations to controller methods

9. **Implement Error Handling**
   - `ApiError.kt` data class
   - `GlobalExceptionHandler.kt` with `@ExceptionHandler` methods

### Phase 4: Documentation
10. **Add OpenAPI Annotations**
    - Add `@Operation`, `@ApiResponse`, `@Parameter`, `@Schema` to controller
    - Configure `OpenAPI` bean in configuration class

11. **Generate OpenAPI YAML**
    - Configure springdoc to generate YAML
    - Save to `src/main/resources/openapi.yaml`

12. **Enable Swagger UI**
    - Access via `/swagger-ui.html`
    - Configure API grouping and display options

### Phase 5: Testing
13. **Write Integration Tests**
    - `ProductControllerIntegrationTest.kt`
    - Test all CRUD operations
    - Test validation scenarios

14. **Write Unit Tests**
    - `ProductServiceTest.kt`
    - `ProductRepositoryTest.kt`

### Phase 6: Documentation & Deployment
15. **Create README.md**
    - API usage examples
    - Setup instructions
    - Swagger UI access details

16. **Update Application Configuration**
    - Review `application.yaml` for any needed changes
    - Add OpenAPI configuration properties

## Dependencies to Add

```kotlin
dependencies {
    // SpringDoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    
    // Validation (if not already included via spring-boot-starter-web)
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // JSON processing for tags field
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
```

## OpenAPI Configuration

```kotlin
@Configuration
class OpenApiConfig {
    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(Info()
                .title("Product API")
                .description("Product management API for msa-product-receiver")
                .version("v1.0.0"))
            .externalDocs(ExternalDocumentation()
                .description("Product API Documentation")
                .url("https://example.com/docs"))
    }
}
```

## File Structure

```
src/main/kotlin/ru/example/product/receiver/
├── MsaProductReceiverApplication.kt
├── config/
│   ├── OpenApiConfig.kt
│   └── DatabaseConfig.kt
├── domain/
│   ├── ProductCategory.kt
│   ├── ProductEntity.kt
│   └── ProductDto.kt
├── dto/
│   ├── request/
│   │   ├── CreateProductRequest.kt
│   │   └── UpdateProductRequest.kt
│   ├── response/
│   │   └── ApiResponse.kt
│   └── error/
│       └── ApiError.kt
├── repository/
│   └── ProductRepository.kt
├── service/
│   ├── ProductService.kt
│   └── ProductServiceImpl.kt
├── controller/
│   ├── ProductController.kt
│   └── exception/
│       └── GlobalExceptionHandler.kt
└── resources/
    ├── application.yaml
    └── openapi.yaml
```

## Testing Strategy

1. **Integration Tests**: Use `@SpringBootTest` with Testcontainers PostgreSQL
2. **Unit Tests**: Mock dependencies for service layer
3. **API Tests**: Test controller endpoints with MockMvc
4. **Validation Tests**: Test all constraint violations

## Success Criteria

- [ ] All CRUD operations work via REST API
- [ ] Validation errors return proper HTTP status and messages
- [ ] Swagger UI accessible at `/swagger-ui.html`
- [ ] OpenAPI YAML generated at `src/main/resources/openapi.yaml`
- [ ] All tests pass
- [ ] API follows RESTful conventions
- [ ] Code follows Kotlin and Spring Boot best practices

## Next Steps

1. Review this plan with stakeholders
2. Begin implementation in Code mode
3. Iterate based on feedback
4. Deploy and monitor