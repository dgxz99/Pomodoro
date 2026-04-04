# Pomodoro App - Phase 1: Project Setup & Data Layer

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Initialize Android project with Jetpack Compose and implement data persistence layer

**Architecture:** Clean architecture with Repository pattern, Room for local storage, DataStore for settings

**Tech Stack:** Kotlin, Jetpack Compose, Room, DataStore Preferences, Hilt (DI)

---

## Task 1: Initialize Android Project

**Files:**
- Create: Project structure via Android Studio

- [ ] **Step 1: Create new Android project**

使用 Android Studio 创建新项目：
- Template: Empty Activity (Compose)
- Name: Pomodoro
- Package name: com.github.dgxz99.pomodoro
- Language: Kotlin
- Minimum SDK: API 24
- Build configuration: Kotlin DSL

- [ ] **Step 2: Update build.gradle.kts (Project level)**

```kotlin
// Top-level build file
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
}
```

- [ ] **Step 3: Update build.gradle.kts (App level)**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.github.dgxz99.pomodoro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.dgxz99.pomodoro"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    
    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.0")
    implementation("androidx.room:room-ktx:2.6.0")
    ksp("androidx.room:room-compiler:2.6.0")
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

- [ ] **Step 4: Sync Gradle**

点击 "Sync Now" 或运行：
```bash
./gradlew build
```

Expected: Build successful

- [ ] **Step 5: Commit**

```bash
git init
git add .
git commit -m "chore: initialize Android project with Compose"
```

---

## Task 2: Define Domain Models

**Files:**
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/domain/model/TimerMode.kt`
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/domain/model/TimerState.kt`

- [ ] **Step 1: Create TimerMode enum**

```kotlin
package com.github.dgxz99.pomodoro.domain.model

enum class TimerMode {
    FOCUS,       // 专注时间
    SHORT_BREAK, // 短休息
    LONG_BREAK   // 长休息
}
```

- [ ] **Step 2: Create TimerState data class**

```kotlin
package com.github.dgxz99.pomodoro.domain.model

data class TimerState(
    val mode: TimerMode = TimerMode.FOCUS,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val currentPomodoroIndex: Int = 0  // 0-based index
)
```

- [ ] **Step 3: Verify compilation**

```bash
./gradlew assembleDebug
```

Expected: Build successful

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/github/dgxz99/pomodoro/domain/
git commit -m "feat: add domain models for timer"
```

---

## Task 3: Setup Room Database

**Files:**
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/data/local/PomodoroRecord.kt`
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/data/local/PomodoroDao.kt`
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/data/local/PomodoroDatabase.kt`

- [ ] **Step 1: Create PomodoroRecord entity**

```kotlin
package com.github.dgxz99.pomodoro.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_records")
data class PomodoroRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val completedAt: Long,  // Unix timestamp in milliseconds
    val duration: Int       // Duration in minutes
)
```

- [ ] **Step 2: Create PomodoroDao interface**

```kotlin
package com.github.dgxz99.pomodoro.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Insert
    suspend fun insert(record: PomodoroRecord)
    
    @Query("SELECT * FROM pomodoro_records ORDER BY completedAt DESC")
    fun getAllRecords(): Flow<List<PomodoroRecord>>
    
    @Query("SELECT * FROM pomodoro_records WHERE completedAt >= :startTimestamp ORDER BY completedAt DESC")
    fun getRecordsSince(startTimestamp: Long): Flow<List<PomodoroRecord>>
    
    @Query("SELECT COUNT(*) FROM pomodoro_records WHERE completedAt >= :startTimestamp")
    suspend fun getCountSince(startTimestamp: Long): Int
    
    @Query("DELETE FROM pomodoro_records")
    suspend fun deleteAll()
}
```

- [ ] **Step 3: Create PomodoroDatabase**

```kotlin
package com.github.dgxz99.pomodoro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PomodoroRecord::class],
    version = 1,
    exportSchema = false
)
abstract class PomodoroDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
    
    companion object {
        @Volatile
        private var INSTANCE: PomodoroDatabase? = null
        
        fun getDatabase(context: Context): PomodoroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PomodoroDatabase::class.java,
                    "pomodoro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

- [ ] **Step 4: Verify compilation**

```bash
./gradlew assembleDebug
```

Expected: Build successful, Room generates DAO implementations

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/github/dgxz99/pomodoro/data/local/
git commit -m "feat: setup Room database for pomodoro records"
```

---

## Task 4: Create Settings DataStore

**Files:**
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/data/preferences/SettingsDataStore.kt`

- [ ] **Step 1: Create SettingsDataStore class**

```kotlin
package com.github.dgxz99.pomodoro.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        val FOCUS_DURATION = intPreferencesKey("focus_duration")
        val SHORT_BREAK_DURATION = intPreferencesKey("short_break_duration")
        val LONG_BREAK_DURATION = intPreferencesKey("long_break_duration")
        val POMODOROS_UNTIL_LONG_BREAK = intPreferencesKey("pomodoros_until_long_break")
        
        // Default values (in minutes)
        const val DEFAULT_FOCUS_DURATION = 25
        const val DEFAULT_SHORT_BREAK_DURATION = 5
        const val DEFAULT_LONG_BREAK_DURATION = 15
        const val DEFAULT_POMODOROS_UNTIL_LONG_BREAK = 4
    }
    
    val focusDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[FOCUS_DURATION] ?: DEFAULT_FOCUS_DURATION
    }
    
    val shortBreakDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[SHORT_BREAK_DURATION] ?: DEFAULT_SHORT_BREAK_DURATION
    }
    
    val longBreakDuration: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[LONG_BREAK_DURATION] ?: DEFAULT_LONG_BREAK_DURATION
    }
    
    val pomodorosUntilLongBreak: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[POMODOROS_UNTIL_LONG_BREAK] ?: DEFAULT_POMODOROS_UNTIL_LONG_BREAK
    }
    
    suspend fun setFocusDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[FOCUS_DURATION] = minutes
        }
    }
    
    suspend fun setShortBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[SHORT_BREAK_DURATION] = minutes
        }
    }
    
    suspend fun setLongBreakDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[LONG_BREAK_DURATION] = minutes
        }
    }
    
    suspend fun setPomodorosUntilLongBreak(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[POMODOROS_UNTIL_LONG_BREAK] = count
        }
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew assembleDebug
```

Expected: Build successful

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/github/dgxz99/pomodoro/data/preferences/
git commit -m "feat: add settings DataStore for user preferences"
```

---

## Task 5: Create Repository

**Files:**
- Create: `app/src/main/java/com/github/dgxz99/pomodoro/data/repository/PomodoroRepository.kt`

- [ ] **Step 1: Create PomodoroRepository class**

```kotlin
package com.github.dgxz99.pomodoro.data.repository

import com.github.dgxz99.pomodoro.data.local.PomodoroDao
import com.github.dgxz99.pomodoro.data.local.PomodoroRecord
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class PomodoroRepository(private val pomodoroDao: PomodoroDao) {
    
    fun getAllRecords(): Flow<List<PomodoroRecord>> {
        return pomodoroDao.getAllRecords()
    }
    
    suspend fun insertRecord(duration: Int) {
        val record = PomodoroRecord(
            completedAt = System.currentTimeMillis(),
            duration = duration
        )
        pomodoroDao.insert(record)
    }
    
    suspend fun getTodayCount(): Int {
        val startOfDay = getStartOfDayTimestamp()
        return pomodoroDao.getCountSince(startOfDay)
    }
    
    suspend fun getWeekCount(): Int {
        val startOfWeek = getStartOfWeekTimestamp()
        return pomodoroDao.getCountSince(startOfWeek)
    }
    
    suspend fun getMonthCount(): Int {
        val startOfMonth = getStartOfMonthTimestamp()
        return pomodoroDao.getCountSince(startOfMonth)
    }
    
    suspend fun getTotalCount(): Int {
        return pomodoroDao.getCountSince(0)
    }
    
    fun getRecordsSince(timestamp: Long): Flow<List<PomodoroRecord>> {
        return pomodoroDao.getRecordsSince(timestamp)
    }
    
    private fun getStartOfDayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfWeekTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun getStartOfMonthTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
```

- [ ] **Step 2: Verify compilation**

```bash
./gradlew assembleDebug
```

Expected: Build successful

- [ ] **Step 3: Commit**

```bash
git add app/src/main/java/com/github/dgxz99/pomodoro/data/repository/
git commit -m "feat: add repository for pomodoro data access"
```

---

## Phase 1 Complete

阶段 1 已完成：
- ✅ Android 项目初始化（Jetpack Compose）
- ✅ Domain models（TimerMode, TimerState）
- ✅ Room 数据库（PomodoroRecord, DAO, Database）
- ✅ DataStore（用户设置）
- ✅ Repository（数据访问层）

**下一阶段**：Phase 2 - 计时功能 + UI 界面
