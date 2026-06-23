# План 06: UI - экран Charts (часть 1: виджеты 1-4)

## Цель
Реализовать основной экран дашборда с первыми 4 графиками.

## Задачи

### 6.1. Создать `DashboardScreen` с LazyVerticalGrid
```kotlin
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // контент карточек
    }
}
```

### 6.2. Создать компонент `DashboardCard` с состояниями
```kotlin
@Composable
fun DashboardCard(
    title: String,
    state: CardState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        when (state) {
            is CardState.Loading -> ShimmerPlaceholder()
            is CardState.Error -> ErrorView(state.message)
            is CardState.Empty -> EmptyView()
            is CardState.Data -> DataContent(state.data)
        }
    }
}
```

### 6.3. Добавить Pull-to-Refresh
```kotlin
var isRefreshing by remember { mutableStateOf(false) }
val pullRefreshRef = pullRefresh(isRefreshing::getValue)

LazyVerticalGrid(
    modifier = Modifier.pullRefresh(pullRefreshRef)
) { ... }

PullRefreshIndicator(
    isRefreshing,
    pullRefreshRef,
    MaterialTheme.colorScheme.primary
)
```

### 6.4. Виджет 1: Переходы по статусам (Vertical Bar Chart)
```kotlin
@Composable
fun StatusTransitionsChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val axisItem = @Composable (label: AxisLabel, _: SpecRequisite) -> TextLabel(label.text)
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text("Переходы по статусам", style = MaterialTheme.typography.titleSmall)
        ViewModel(
            model = staticAxisModel(
                entries = data.entries.mapIndexed { index, (key, value) ->
                    entry(key, value, ChartColors[index % ChartColors.size])
                }
            ),
            modifier = Modifier.height(200.dp)
        ) {
            bar { barData() }
        }
    }
}
```

### 6.5. Виджет 2: Среднее время обработки (Single Bar / Gauge)
```kotlin
@Composable
fun AvgProcessingTimeChart(
    avgTime: Double?,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Среднее время обработки", style = MaterialTheme.typography.titleSmall)
            if (avgTime != null) {
                Text(
                    "${String.format("%.0f", avgTime)} мс",
                    style = MaterialTheme.typography.headlineMedium
                )
            } else {
                Text("Нет данных", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
```

### 6.6. Виджет 3: Топ продуктов (Horizontal Bar Chart)
```kotlin
@Composable
fun TopProductsChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Топ продуктов", style = MaterialTheme.typography.titleSmall)
            ViewModel(
                model = staticAxisModel(
                    entries = data.entries.mapIndexed { index, (key, value) ->
                        entry(key, value, ChartColors[index % ChartColors.size])
                    },
                    horizontal = true
                ),
                modifier = Modifier.height(200.dp)
            ) {
                bar { barData() }
            }
        }
    }
}
```

### 6.7. Виджет 4: Причины переходов (Vertical Bar Chart)
```kotlin
@Composable
fun TransitionReasonsChart(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Причины переходов", style = MaterialTheme.typography.titleSmall)
            ViewModel(
                model = staticAxisModel(
                    entries = data.entries.mapIndexed { index, (key, value) ->
                        entry(key, value, ChartColors[index % ChartColors.size])
                    }
                ),
                modifier = Modifier.height(200.dp)
            ) {
                bar { barData() }
            }
        }
    }
}
```

### 6.8. Добавить тултипы для всех графиков
```kotlin
@Composable
fun TooltipOverlay(
    tooltipData: TooltipData?,
    modifier: Modifier = Modifier
) {
    tooltipData?.let {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text("${it.label}: ${it.value}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

## Критерии готовности
- [ ] DashboardScreen создан с LazyVerticalGrid
- [ ] DashboardCard компонент с 4 состояниями работает
- [ ] Pull-to-Refresh реализован
- [ ] Виджет 1: Vertical Bar Chart для статусов
- [ ] Виджет 2: Single Bar для времени обработки
- [ ] Виджет 3: Horizontal Bar Chart для топ продуктов
- [ ] Виджет 4: Vertical Bar Chart для причин
- [ ] Тултипы работают на графиках
