# План 02: Data Layer - работа с API

## Цель
Реализовать взаимодействие с бэкенд API для получения истории статусов продуктов.

## Задачи

### 2.1. Создать модель `StatusHistoryRecord`
```kotlin
data class StatusHistoryRecord(
    val id: String,
    val productId: String,
    val fromStatus: String?,
    val toStatus: String,
    val reason: String?,
    val timestamp: String,
    val userId: String?,
    val processingTimeMs: Long?
)
```

### 2.2. Создать обертку ответа `ApiResponse<T>`
```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?
)
```

### 2.3. Создать интерфейс API `StatusHistoryApi`
```kotlin
interface StatusHistoryApi {
    @GET("api/v1/status-history")
    suspend fun getStatusHistory(
        @Query("limit") limit: Int
    ): ApiResponse<List<StatusHistoryRecord>>
}
```

### 2.4. Настроить Retrofit с OkHttp
- Kotlin Serialization converter
- Logging interceptor (для debug)
- Base URL из BuildConfig
- Timeout настройки (connect: 30s, read: 60s)

### 2.5. Создать `ApiModule` для Hilt
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideStatusHistoryApi(): StatusHistoryApi { ... }
}
```

### 2.6. Реализовать `DashboardRepository`
```kotlin
interface DashboardRepository {
    suspend fun getStatusHistory(limit: Int): Result<List<StatusHistoryRecord>>
}

class DashboardRepositoryImpl @Inject constructor(
    private val api: StatusHistoryApi
) : DashboardRepository {
    override suspend fun getStatusHistory(limit: Int): Result<List<StatusHistoryRecord>> { ... }
}
```

### 2.7. Добавить обработку ошибок
- HTTP 4xx → `ApiException` с кодом
- HTTP 5xx → `ServerException`
- Network Error → `NetworkException`
- Parse Error → `ParseException`

### 2.8. Добавить AuthInterceptor для Bearer Token
```kotlin
class AuthInterceptor @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = dataStore.getDataStoreToken()
        return chain.proceed(chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build())
    }
}
```

## Критерии готовности
- [ ] Модель StatusHistoryRecord создана
- [ ] ApiResponse обертка создана
- [ ] Интерфейс StatusHistoryApi реализован
- [ ] Retrofit настроен с OkHttp и Logging
- [ ] Hilt модуль для API создан
- [ ] Repository реализован
- [ ] Обработка ошибок реализована
- [ ] AuthInterceptor добавлен
