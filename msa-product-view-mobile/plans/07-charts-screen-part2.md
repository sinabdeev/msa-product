# План 07: UI - экран Charts (часть 2: виджеты 5-9)

## Цель
Реализовать оставшиеся графики дашборда (Pie, Donut, Radial charts).

## Задачи

### 7.1. Виджет 5: Доля статусов (Pie Chart)
```kotlin
@Composable
fun StatusShareChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Доля статусов", style = MaterialTheme.typography.titleSmall)
            ViewModel(
                model = pieModel(
                    entries = data.entries.mapIndexed { index, (key, value) ->
                        pieEntry(key, value, ChartColors[index % ChartColors.size])
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                pie { pieData() }
            }
        }
    }
}
```

### 7.2. Виджет 6: Доля причин (Donut Chart)
```kotlin
@Composable
fun ReasonShareChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Доля причин", style = MaterialTheme.typography.titleSmall)
            ViewModel(
                model = pieModel(
                    entries = data.entries.mapIndexed { index, (key, value) ->
                        pieEntry(key, value, ChartColors[index % ChartColors.size])
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                pie { pieData() }
                pieCenterContent {
                    Text("Причины", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}
```

### 7.3. Виджет 7: Откуда переходят (Pie Chart)
```kotlin
@Composable
fun SourceStatusesChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Откуда переходят", style = MaterialTheme.typography.titleSmall)
            ViewModel(
                model = pieModel(
                    entries = data.entries.mapIndexed { index, (key, value) ->
                        pieEntry(key, value, ChartColors[index % ChartColors.size])
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                pie { pieData() }
            }
        }
    }
}
```

### 7.4. Виджет 8: Активность по часам (Radial Bar Chart)
```kotlin
@Composable
fun HourlyActivityChart(
    data: Map<Int, Int>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Активность по часам", style = MaterialTheme.typography.titleSmall)
            ViewModel(
                model = radialAxisModel(
                    entries = (0..23).map { hour ->
                        radialEntry(
                            label = "$hour:00",
                            value = data[hour] ?: 0,
                            color = ChartColors[hour % ChartColors.size]
                        )
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                radialBar { radialBarData() }
            }
        }
    }
}
```

### 7.5. Виджет 9: Активность пользователей (Заглушка)
```kotlin
@Composable
fun UserActivityPlaceholder(
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Данные об активности пользователей отсутствуют",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### 7.6. Интеграция всех виджетов в DashboardScreen
```kotlin
// В DashboardScreen:
items(
    count = 9,
    key = { index -> "widget_$index" }
) { index ->
    DashboardCard(
        title = getWidgetTitle(index),
        state = getWidgetState(index),
        modifier = Modifier.fillMaxWidth()
    )
}
```

### 7.7. Добавить Shimmer-эффект для loading-состояния
```kotlin
@Composable
fun ShimmerPlaceholder() {
    val shimmer = rememberShimmerTransition()
    Column {
        Shimmer(
            shimmer = shimmer,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
```

## Критерии готовности
- [ ] Виджет 5: Pie Chart для доли статусов
- [ ] Виджет 6: Donut Chart для доли причин
- [ ] Виджет 7: Pie Chart для source statuses
- [ ] Виджет 8: Radial Bar Chart для активности по часам
- [ ] Виджет 9: Заглушка для активности пользователей
- [ ] Все виджеты интегрированы в DashboardScreen
- [ ] Shimmer-эффект для loading работает
- [ ] Тултипы на Pie/Donut/Radial графиках работают
