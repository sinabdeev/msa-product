# План 01: Инициализация проекта и настройка инфраструктуры

## Цель
Создать базовую структуру Android-проекта и настроить все необходимые зависимости.

## Задачи

### 1.1. Создать проект в Android Studio
- Empty Activity with Compose
- Минимальный SDK: API 26 (Android 8.0)
- Язык: Kotlin
- Название: `product-status-history-dashboard`

### 1.2. Настроить `build.gradle.kts` (project level)
```kotlin
plugins {
    id("com.android.application") version "8.x"
    id("org.jetbrains.kotlin.android") version "2.x"
    id("com.google.dagger.hilt.android") version "2.x"
    id("com.google.devtools.ksp") version "1.x"
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.x"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // для Vico
    }
}
```

### 1.3. Настроить `build.gradle.kts` (app level) - зависимости
```kotlin
dependencies {
    // Compose
    implementation("androidx.compose.ui:ui:1.x")
    implementation("androidx.compose.material3:material3:1.x")
    implementation("androidx.navigation:navigation-compose:2.x")
    
    // Vico (графики)
    implementation("com.patrykandpatrick.vico:compose:1.x")
    
    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.x")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.x")
    implementation("com.squareup.okhttp3:okhttp:4.x")
    implementation("com.squareup.okhttp3:logging-interceptor:4.x")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.x")
    ksp("com.google.dagger:hilt-compiler:2.x")
    
    // Room
    implementation("androidx.room:room-runtime:2.x")
    implementation("androidx.room:room-ktx:2.x")
    ksp("androidx.room:room-compiler:2.x")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.x")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.x")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.x")
    
    // Timber
    implementation("com.jakewharton.timber:timber:5.x")
    
    // Security Crypto
    implementation("androidx.security:security-crypto:1.x")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.x")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.x")
}
```

### 1.4. Создать структуру пакетов (Clean Architecture)
```
com.example.productdashboard
├── di/                    # Hilt modules
├── data/
│   ├── api/              # Retrofit interfaces
│   ├── model/            # Data models
│   ├── repository/       # Repository implementations
│   ├── room/             # Room entities, DAOs, Database
│   └── datastore/        # DataStore helpers
├── domain/
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Use cases
├── ui/
│   ├── theme/            # Colors, Typography, Shapes
│   ├── navigation/       # NavGraph, routes
│   ├── charts/           # Dashboard screen
│   ├── data/             # Data screen
│   ├── settings/         # Settings screen
│   └── components/       # Reusable UI components
└── util/                 # Extensions, formatters
```

### 1.5. Настроить Hilt для Dependency Injection
- Создать `@HiltAndroidApp` класс `ProductDashboardApp`
- Создать `AndroidManifest.xml` с указанием application class
- Создать базовые Hilt модули

### 1.6. Настроить Timber для логирования
- Инициализация в Application class
- `Timber.DebugTree()` для debug
- `Timber.Tree()` для release

### 1.7. Настроить BuildConfig для Base URL
- Использовать `secrets-gradle-plugin`
- `local.properties` для секретов
- `BuildConfig.API_BASE_URL`

## Критерии готовности
- [ ] Проект собирается и запускается
- [ ] Структура пакетов создана
- [ ] Hilt настроен и работает
- [ ] Timber инициализирован
- [ ] Secrets plugin настроен
- [ ] Все зависимости добавлены
