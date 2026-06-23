# План 09: UI - экран Settings (настройки)

## Цель
Реализовать экран настроек приложения с сохранением в DataStore.

## Задачи

### 9.1. Создать `SettingsScreen` с настройками
```kotlin
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.settingsState.collectAsState()
    
    Scaffold(
        topBar = { TopAppBar(title = { Text("Настройки") }) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingsSection(title = "Основные") {
                LimitInput(state.initialLimit) { viewModel.updateLimit(it) }
                PollIntervalInput(state.pollInterval) { viewModel.updatePollInterval(it) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.resetToDefaults() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сбросить по умолчанию")
            }
        }
    }
}
```

### 9.2. Добавить поле Initial Limit (default: 50)
```kotlin
@Composable
fun LimitInput(
    limit: Int,
    onLimitChanged: (Int) -> Unit
) {
    TextField(
        value = limit.toString(),
        onValueChange = { onLimitChanged(it.toIntOrNull() ?: 50) },
        label = { Text("Initial Limit") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}
```

### 9.3. Добавить поле Poll Interval (default: 30 сек)
```kotlin
@Composable
fun PollIntervalInput(
    interval: Int,
    onIntervalChanged: (Int) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = "$interval сек",
            onValueChange = {},
            label = { Text("Poll Interval") },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(15, 30, 60, 120, 300).forEach { seconds ->
                DropdownMenuItem(
                    text = { Text("$seconds сек") },
                    onClick = { onIntervalChanged(seconds); expanded = false }
                )
            }
        }
    }
}
```

### 9.4. Добавить кнопку "Reset to Defaults"
```kotlin
@Composable
fun SettingsViewModel(
    private val saveSettingsUseCase: SaveSettingsUseCase
) : ViewModel() {
    val settingsState = MutableStateFlow(SettingsState())
    
    fun resetToDefaults() {
        saveSettingsUseCase.saveDefaults()
        settingsState.value = SettingsState()
    }
}
```

### 9.5. Настроить DataStore Preferences для хранения настроек
```kotlin
object PreferencesKeys {
    val INITIAL_LIMIT = intPreferencesKey("initial_limit")
    val POLL_INTERVAL = intPreferencesKey("poll_interval")
}

class AppDataStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore: DataStore<Preferences> = context.createDataStore("settings")
    
    val settingsFlow: Flow<Settings> = dataStore.data
        .catch { emit(defaultSettings) }
        .map { prefs ->
            Settings(
                initialLimit = prefs[PreferencesKeys.INITIAL_LIMIT] ?: 50,
                pollInterval = prefs[PreferencesKeys.POLL_INTERVAL] ?: 30
            )
        }
    
    suspend fun saveSettings(settings: Settings) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.INITIAL_LIMIT] = settings.initialLimit
            prefs[PreferencesKeys.POLL_INTERVAL] = settings.pollInterval
        }
    }
    
    suspend fun resetToDefaults() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
```

## Критерии готовности
- [ ] SettingsScreen создан
- [ ] Поле Initial Limit работает (default: 50)
- [ ] Поле Poll Interval работает (default: 30 сек)
- [ ] Кнопка "Reset to Defaults" работает
- [ ] DataStore сохраняет настройки
- [ ] Настройки применяются немедленно
- [ ] SettingsViewModel реализован
