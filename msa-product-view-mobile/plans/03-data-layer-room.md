# План 03: Data Layer - локальное кэширование (Room)

## Цель
Реализовать локальное кэширование данных через Room для работы оффлайн и ускорения загрузки.

## Задачи

### 3.1. Создать Entity `StatusHistoryEntity`
```kotlin
@Entity(tableName = "status_history")
data class StatusHistoryEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val fromStatus: String?,
    val toStatus: String,
    val reason: String?,
    val timestamp: String,
    val userId: String?,
    val processingTimeMs: Long?
)
```

### 3.2. Создать DAO `StatusHistoryDao`
```kotlin
@Dao
interface StatusHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<StatusHistoryEntity>)
    
    @Query("SELECT * FROM status_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<StatusHistoryEntity>>
    
    @Query("DELETE FROM status_history")
    suspend fun clear()
    
    @Query("SELECT COUNT(*) FROM status_history")
    suspend fun getCount(): Int
}
```

### 3.3. Создать `AppDatabase` с Hilt модулем
```kotlin
@Database(entities = [StatusHistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun statusHistoryDao(): StatusHistoryDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "product-dashboard-db")
            .build()
    }
}
```

### 3.4. Реализовать `LocalDataSource`
```kotlin
class LocalDataSource @Inject constructor(
    private val dao: StatusHistoryDao
) {
    val dataFlow: Flow<Result<List<StatusHistoryRecord>>> = dao.getAll()
        .map { entities ->
            Result.success(entities.map { it.toDomain() })
        }
        .catch { emit(Result.failure(it)) }
    
    suspend fun cacheData(records: List<StatusHistoryRecord>) {
        dao.insertAll(records.map { it.toEntity() })
    }
    
    suspend fun invalidateCache() {
        dao.clear()
    }
}
```

### 3.5. Добавить кэширование в Repository
```kotlin
override suspend fun getStatusHistory(limit: Int): Result<List<StatusHistoryRecord>> {
    return try {
        val response = api.getStatusHistory(limit).data
        if (response != null) {
            localDataSource.cacheData(response)
            Result.success(response)
        } else {
            Result.failure(Exception("Empty response"))
        }
    } catch (e: Exception) {
        // При ошибке сети возвращаем кэш
        localDataSource.dataFlow.first()
    }
}
```

### 3.6. Реализовать инвалидацию кэша при Pull-to-Refresh
- Метод `invalidateCache()` в Repository
- Вызов при refresh из UI
- Автоматическая перезагрузка из API

## Критерии готовности
- [ ] Entity StatusHistoryEntity создан
- [ ] DAO StatusHistoryDao реализован
- [ ] AppDatabase создан и настроен через Hilt
- [ ] LocalDataSource реализован
- [ ] Кэширование работает в Repository
- [ ] Инвалидация кэша при refresh работает
- [ ] Оффлайн-режим проверяется
