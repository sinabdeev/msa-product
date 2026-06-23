# План 04: Domain Layer - агрегация данных

## Цель
Реализовать логику агрегации сырых данных статусов в метрики для графиков дашборда.

## Задачи

### 4.1. Создать модель `AggregatedDashboard`
```kotlin
data class AggregatedDashboard(
    val statusTransitions: Map<String, Int>,
    val avgProcessingTime: Double?,
    val topProducts: Map<String, Int>,
    val transitionReasons: Map<String, Int>,
    val statusShare: Map<String, Double>,
    val reasonShare: Map<String, Double>,
    val sourceStatuses: Map<String, Int>,
    val hourlyActivity: Map<Int, Int>,
    val totalRecords: Int
)
```

### 4.2. Реализовать функцию агрегации `toAggregatedData()`
```kotlin
fun List<StatusHistoryRecord>.toAggregatedData(): AggregatedDashboard {
    val statusTransitions = groupBy { it.toStatus }
        .mapValues { it.value.size }
    
    val avgProcessingTime = filterNotNull { it.processingTimeMs }
        .takeIf { it.isNotEmpty() }
        ?.mapNotNull { it.processingTimeMs }
        ?.average()
    
    val topProducts = groupBy { it.productId }
        .mapValues { it.value.size }
        .toSortedMap { a, b -> this[b]!!.compareTo(this[a]!!) }
        .let { m -> m.entries.take(10).associate { it.key to it.value } }
    
    val transitionReasons = groupBy { it.reason ?: "Unknown" }
        .mapValues { it.value.size }
    
    val statusShare = statusTransitions.entries.let { entries ->
        val total = entries.sumOf { it.value }
        entries.associate { it.key to (it.value * 100.0 / total) }
    }
    
    val reasonShare = transitionReasons.entries.let { entries ->
        val total = entries.sumOf { it.value }
        entries.associate { it.key to (it.value * 100.0 / total) }
    }
    
    val sourceStatuses = groupBy { it.fromStatus ?: "Unknown" }
        .mapValues { it.value.size }
    
    val hourlyActivity = groupBy { 
        LocalDateTime.parse(it.timestamp).hour 
    }.mapValues { it.value.size }
    
    return AggregatedDashboard(
        statusTransitions, avgProcessingTime, topProducts,
        transitionReasons, statusShare, reasonShare,
        sourceStatuses, hourlyActivity, size
    )
}
```

### 4.3. Создать UseCase `AggregateDashboardDataUseCase`
```kotlin
class AggregateDashboardDataUseCase @Inject constructor() {
    operator fun invoke(records: List<StatusHistoryRecord>): AggregatedDashboard {
        return records.toAggregatedData()
    }
}
```

### 4.4. Реализовать маппинг для графиков
- StatusTransitions → Vertical Bar Chart
- AvgProcessingTime → Single Bar / Gauge
- TopProducts → Horizontal Bar Chart
- TransitionReasons → Vertical Bar Chart
- StatusShare → Pie Chart
- ReasonShare → Donut Chart
- SourceStatuses → Pie Chart
- HourlyActivity → Radial Bar / Pie

## Критерии готовности
- [ ] Модель AggregatedDashboard создана
- [ ] Функция toAggregatedData() реализована
- [ ] UseCase создан
- [ ] Все 8 метрик агрегируются корректно
- [ ] Unit-тесты для агрегации (опционально)
