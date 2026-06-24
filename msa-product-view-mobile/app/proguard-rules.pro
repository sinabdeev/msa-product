# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /sdk/tools/proguard/proguard-android.txt

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontokhttp3.**
-keep class okhttp3.** { *; }
-keepattributes *Annotation*

# Kotlin
-keepattributes *Annotation*, *Fields*, *Methods*, *Classes*, *InnerClasses*
-dontwarn kotlin.**
-keep class kotlin.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt
-dontwarn javax.**
-dontwarn generated.**
-keep,dontwarn com.example.msaproductviewmobile.**
-keep @dagger.hilt.android.HiltAndroidApp com.example.msaproductviewmobile.**
-keep @dagger.hilt.android.HiltAndroidActivity **
-keep @dagger.hilt.android.HiltAndroidFragment **
-keep @dagger.hilt.android.HiltAndroidService **
-keep @dagger.hilt.android.HiltAndroidApplication **
-keep @dagger.Binds **
-keep @dagger.Module **
-keep @dagger.Provides **

# Room
-keep class * extends android.arch.persistence.room.RoomDatabase

# Timber
-keep class com.jakewharton.timber.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
