# План тестирования системы управления статусами продукта

## Цель
Обеспечить покрытие тестами всех возможных переходов статусов, включая комбинирование путей, и проверить корректность работы системы.

## Типы тестов

### 1. Unit-тесты
- **ProductStatusTransitionValidatorTest** — тестирование матрицы переходов
- **ProductStatusTransitionServiceTest** — тестирование логики перехода (моки зависимостей)
- **ProductStatusHistoryServiceTest** — тестирование записи истории
- **ProductServiceImplTest** — тестирование новых методов (моки)

### 2. Интеграционные тесты
- **ProductStatusTransitionIntegrationTest** — тесты с реальной базой данных, проверяющие полный цикл перехода
- **ProductServiceIntegrationTest** — тесты с реальным Spring контекстом
- **PathCombinationTest** — тесты комбинирования путей (последовательности переходов)

### 3. End-to-end тесты (если есть REST API)
- **ProductControllerTest** — тесты REST endpoints для изменения статуса

## Тестовые сценарии

### Сценарий 1: Валидация переходов
Проверить все допустимые и недопустимые переходы согласно матрице.

**Тестовые данные**:
- Для каждого статуса `current` проверить переход во все возможные `target` (разрешенные)
- Для каждого статуса `current` проверить переход в случайные неразрешенные `target` (запрещенные)

**Ожидаемые результаты**:
- `isTransitionAllowed` возвращает `true` для разрешенных переходов
- `isTransitionAllowed` возвращает `false` для запрещенных переходов

### Сценарий 2: Полные пути (из документации)
Протестировать каждый из 10 путей, описанных в `product_status_workflow.md`.

**Пример для пути 1 (успешный workflow)**:
1. Создать продукт со статусом `DRAFT`
2. Перевести в `PENDING_REVIEW` → успех
3. Перевести в `REVIEWED` → успех
4. Перевести в `APPROVED` → успех
5. Перевести в `ACTIVE` → успех
6. Перевести в `PROCESSED` → успех
7. Перевести в `SHIPPED` → успех
8. Перевести в `ARCHIVED` → успех

**Проверки**:
- После каждого перехода статус продукта соответствует ожидаемому
- В истории переходов появляется запись
- Событие публикуется (если настроено)

### Сценарий 3: Комбинирование путей
Проверить возможность комбинирования переходов на лету.

**Пример комбинированного пути**:
1. `DRAFT` → `PENDING_REVIEW`
2. `PENDING_REVIEW` → `REVIEWED`
3. `REVIEWED` → `REJECTED` (отклонение)
4. `REJECTED` → `PENDING_REVIEW` (повторная отправка)
5. `PENDING_REVIEW` → `REVIEWED`
6. `REVIEWED` → `APPROVED`
7. `APPROVED` → `ACTIVE`
8. `ACTIVE` → `ARCHIVED` (досрочное архивирование)

**Проверки**:
- Все переходы выполняются успешно
- История содержит все промежуточные статусы
- Финальный статус `ARCHIVED`

### Сценарий 4: Ошибки и исключения
Проверить обработку ошибочных ситуаций:
- Попытка перехода в недопустимый статус → `InvalidStatusTransitionException`
- Попытка изменить статус несуществующего продукта → `ProductNotFoundException`
- Переход с `null` статусом (если продукт создан без статуса) → дефолтный `DRAFT`

### Сценарий 5: Бизнес-правила (дополнительные валидаторы)
Если реализованы правила типа "нельзя возвращаться в черновик после утверждения":
- Попытка перехода `APPROVED` → `DRAFT` → исключение
- Попытка перехода `APPROVED` → `ACTIVE` → успех

### Сценарий 6: Получение возможных переходов
Проверить метод `getPossibleTransitions` для каждого статуса:
- Для `DRAFT` возвращает `{PENDING_REVIEW, ARCHIVED}`
- Для `REVIEWED` возвращает `{APPROVED, REJECTED}`
- И т.д.

## Техническая реализация

### Структура тестовых классов

```
src/test/kotlin/ru/example/product/processing/
├── service/status/
│   ├── ProductStatusTransitionValidatorTest.kt
│   ├── ProductStatusTransitionServiceTest.kt
│   └── ProductStatusHistoryServiceTest.kt
├── service/ProductServiceImplTest.kt
├── integration/
│   ├── ProductStatusTransitionIntegrationTest.kt
│   ├── ProductServiceIntegrationTest.kt
│   └── PathCombinationTest.kt
└── repository/ProductRepositoryTest.kt (если требуется)
```

### Используемые технологии
- **JUnit 5** — фреймворк для тестирования
- **Kotest** — альтернатива (опционально)
- **MockK** или **Mockito** — мокирование зависимостей
- **Testcontainers** или **H2** — база данных для интеграционных тестов
- **Spring Boot Test** — для интеграционных тестов с Spring контекстом

### Настройка тестовой базы данных
Для интеграционных тестов использовать H2 in-memory базу с схемой, идентичной PostgreSQL.

**application-test.yaml**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    username: sa
    password:
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

## Пример unit-теста для валидатора

```kotlin
package ru.example.product.processing.service.status

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import ru.example.product.processing.domain.ProductStatus

class ProductStatusTransitionValidatorTest {

    private val validator = ProductStatusTransitionValidatorImpl()

    @Test
    fun `should allow draft to pending review`() {
        assertTrue(validator.isTransitionAllowed(ProductStatus.DRAFT, ProductStatus.PENDING_REVIEW))
    }

    @Test
    fun `should reject draft to reviewed`() {
        assertFalse(validator.isTransitionAllowed(ProductStatus.DRAFT, ProductStatus.REVIEWED))
    }

    @Test
    fun `should return correct allowed transitions for draft`() {
        val allowed = validator.getAllowedTransitions(ProductStatus.DRAFT)
        assertEquals(setOf(ProductStatus.PENDING_REVIEW, ProductStatus.ARCHIVED), allowed)
    }
}
```

## Пример интеграционного теста для пути

```kotlin
package ru.example.product.processing.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import ru.example.product.processing.domain.ProductStatus
import ru.example.product.processing.service.ProductService
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest
@Transactional
class PathCombinationTest {

    @Autowired
    lateinit var productService: ProductService

    @Test
    fun `test successful workflow path`() {
        // Создание продукта (в реальности через репозиторий)
        val productId = UUID.randomUUID()
        // ... создание продукта со статусом DRAFT

        // Выполнение переходов
        productService.updateStatus(productId, ProductStatus.PENDING_REVIEW)
        productService.updateStatus(productId, ProductStatus.REVIEWED)
        productService.updateStatus(productId, ProductStatus.APPROVED)
        productService.updateStatus(productId, ProductStatus.ACTIVE)
        productService.updateStatus(productId, ProductStatus.PROCESSED)
        productService.updateStatus(productId, ProductStatus.SHIPPED)
        productService.updateStatus(productId, ProductStatus.ARCHIVED)

        // Проверка финального статуса
        val product = productRepository.findById(productId).get()
        assertEquals(ProductStatus.ARCHIVED, product.status)
    }
}
```

## Покрытие кода
Стремиться к покрытию:
- **Валидатор**: 100% (все переходы)
- **Сервис переходов**: 90% (основные сценарии + ошибки)
- **История**: 80% (запись и чтение)
- **Интеграционные тесты**: все пути из документации

## График тестирования
1. **День 1**: Написание unit-тестов для валидатора и сервиса переходов
2. **День 2**: Написание интеграционных тестов для отдельных переходов
3. **День 3**: Написание тестов для комбинирования путей
4. **День 4**: Написание тестов для ошибок и бизнес-правил
5. **День 5**: Рефакторинг тестов, измерение покрытия, исправление багов

## Заключение
Комплексное тестирование обеспечит:
- Корректность работы системы управления статусами
- Отсутствие регрессий при изменении матрицы переходов
- Надежность в production-среде
- Документирование ожидаемого поведения через тесты