# План 05: Design System и навигация

## Цель
Создать дизайн-систему (цвета, темы) и настроить навигацию между экранами.

## Задачи

### 5.1. Создать `colors.kt` с цветовыми константами бренда
```kotlin
// Основные цвета
val PrimaryColor = Color(0xFF6750A4)
val PrimaryContainerColor = Color(0xFFEADDFF)

// Цвета статусов
val ArchivedColor = Color(0xFF8E86D6)      // Фиолетовый
val DraftColor = Color(0xFF8E86D6)          // Фиолетовый
val PendingReviewColor = Color(0xFF9ACD9A)  // Зеленый
val ApprovedColor = Color(0xFF9ACD9A)       // Зеленый
val RejectedColor = Color(0xFFF28C38)       // Оранжевый
val ReviewedColor = Color(0xFFF28C38)       // Оранжевый
val RejectedLightColor = Color(0xFFFAD7A0)  // Желтый

// Цвета для графиков
val ChartColors = listOf(
    Color(0xFF6750A4),
    Color(0xFF9ACD9A),
    Color(0xFFF28C38),
    Color(0xFFFAD7A0),
    Color(0xFF8E86D6),
    Color(0xFF0061A4),
    Color(0xFF006D6B),
    Color(0xFF7D5260)
)
```

### 5.2. Создать `themes.kt` с Material 3 темой
```kotlin
val LightColorScheme = colorScheme(
    primary = PrimaryColor,
    primaryContainer = PrimaryContainerColor,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F)
)

val DarkColorScheme = colorScheme(
    primary = Color(0xFFD0BCFF),
    primaryContainer = Color(0xFF4F378B),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun ProductDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
```

### 5.3. Настроить цветовую схему для графиков
- Определить `ChartColors` для Vico
- Обеспечить контрастность для dark mode
- Добавить градиенты для pie charts

### 5.4. Создать `BottomNavigation` с 3 вкладками
```kotlin
sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Charts : Screen("charts", "Графики", Icons.Default.BarChart)
    object Data : Screen("data", "Данные", Icons.Default.Table)
    object Settings : Screen("settings", "Настройки", Icons.Default.Settings)
}
```

### 5.5. Настроить Navigation Compose
```kotlin
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Charts.route) {
        composable(Screen.Charts.route) { ChartsScreen() }
        composable(Screen.Data.route) { DataScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}
```

### 5.6. Добавить иконки для вкладок
- Charts: BarChart icon
- Data: Table icon
- Settings: Settings icon

## Критерии готовности
- [ ] colors.kt создан с бренд-цветами
- [ ] themes.kt создан с light/dark темами
- [ ] Цвета графиков настроены
- [ ] Bottom Navigation с 3 вкладками работает
- [ ] Navigation Compose настроен
- [ ] Иконки для вкладок добавлены
- [ ] Переключение тем работает
