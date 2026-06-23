# План 08: UI - экран Data (таблица)

## Цель
Реализовать экран отображения сырых записей истории статусов в табличном виде.

## Задачи

### 8.1. Создать `DataScreen` с LazyColumn
```kotlin
@Composable
fun DataScreen(
    viewModel: DataViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.dataState.collectAsState()
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("История статусов") }) }
    ) { padding ->
        when (state) {
            is DataState.Loading -> LoadingView()
            is DataState.Error -> ErrorView((state as DataState.Error).message)
            is DataState.Success -> DataList((state as DataState.Success).records, modifier.padding(padding))
        }
    }
}
```

### 8.2. Отобразить сырые записи StatusHistoryRecord
```kotlin
@Composable
fun DataList(
    records: List<StatusHistoryRecord>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(records, key = { it.id }) { record ->
            DataRecordCard(record)
        }
    }
}

@Composable
fun DataRecordCard(
    record: StatusHistoryRecord,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Продукт: ${record.productId}", style = MaterialTheme.typography.bodyMedium)
                Text(formatTimestamp(record.timestamp), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(record.fromStatus ?: "—")
                ArrowIcon()
                Chip(record.toStatus)
            }
            record.reason?.let { reason ->
                Spacer(modifier = Modifier.height(4.dp))
                Text("Причина: $reason", style = MaterialTheme.typography.bodySmall)
            }
            record.processingTimeMs?.let { ms ->
                Text("Время обработки: ${ms} мс", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
```

### 8.3. Реализовать пагинацию (бесконечный скролл)
```kotlin
@Composable
fun DataScreenWithPagination(
    viewModel: DataViewModel = hiltViewModel()
) {
    val state by viewModel.dataState.collectAsState()
    val lazyListState = rememberLazyListState()
    
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo.lastOrNull() }
            .collect { lastItem ->
                lastItem?.index?.let { index ->
                    if (index >= state.records.size - 5 && !state.isLoading) {
                        viewModel.loadMore()
                    }
                }
            }
    }
}
```

### 8.4. Добавить форматирование timestamp и статусов
```kotlin
fun formatTimestamp(timestamp: String): String {
    val date = LocalDateTime.parse(timestamp)
    return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
}

fun formatStatus(status: String): String {
    return when (status) {
        "ARCHIVED" -> "Архивирован"
        "DRAFT" -> "Черновик"
        "PENDING_REVIEW" -> "На проверке"
        "APPROVED" -> "Одобрен"
        "REJECTED" -> "Отклонен"
        "REVIEWED" -> "Проверен"
        else -> status
    }
}
```

## Критерии готовности
- [ ] DataScreen создан с LazyColumn
- [ ] Сырые записи отображаются в карточках
- [ ] Пагинация (бесконечный скролл) работает
- [ ] Timestamp форматируется
- [ ] Статусы форматируются на русский
- [ ] Pull-to-Refresh для обновления данных
