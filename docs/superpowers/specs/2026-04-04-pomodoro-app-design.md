# Pomodoro Android App Design Spec

## Overview

一个简洁精美的 Android 番茄钟应用，帮助用户专注工作和学习。

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **最低 SDK**: API 24 (Android 7.0)
- **目标 SDK**: API 34 (Android 14)

## 功能规格

### 1. 番茄计时

#### 1.1 计时模式

| 模式 | 默认时长 | 可配置范围 |
|------|----------|------------|
| 专注时间 | 25 分钟 | 1-120 分钟 |
| 短休息 | 5 分钟 | 1-20 分钟 |
| 长休息 | 15 分钟 | 1-60 分钟 |

#### 1.2 番茄周期

- 默认：每 4 个番茄后触发长休息
- 可配置范围：2-8 个番茄
- 进度指示：用圆点显示当前是第几个番茄（圆点数量随配置动态变化）

#### 1.3 计时控制

- **开始**: 启动计时器
- **暂停**: 暂停当前计时，可恢复
- **停止**: 终止当前计时，不计入统计

#### 1.4 计时流程

```
专注时间 → 短休息 → 专注时间 → 短休息 → 专注时间 → 短休息 → 专注时间 → 长休息 → (循环)
```

### 2. 统计功能

#### 2.1 统计数据

| 统计项 | 说明 |
|--------|------|
| 今日番茄数 | 当天完成的番茄数量 |
| 本周番茄数 | 本周（周一至周日）完成的番茄数量 |
| 本月番茄数 | 本月完成的番茄数量 |
| 总计番茄数 | 历史累计完成的番茄数量 |
| 连续使用天数 | 连续每天至少完成 1 个番茄的天数 |

#### 2.2 数据持久化

- 使用 Room 数据库存储番茄记录
- 记录字段：完成时间、时长

### 3. 通知功能

#### 3.1 计时结束通知

- 番茄/休息结束时发送系统通知
- 使用 Android 系统内置简洁提示音
- 通知内容：提示当前阶段结束，引导下一阶段

#### 3.2 后台计时

- 应用进入后台时计时继续
- 使用 Foreground Service 确保计时准确

## UI 设计规格

### 1. 设计风格

**番茄红暖色风格**

| 颜色 | 色值 | 用途 |
|------|------|------|
| 主色 | #ff6b6b | 计时器、强调元素、选中状态 |
| 背景色 | #fef9f3 | 页面背景 |
| 卡片色 | #ffffff | 统计卡片、按钮背景 |
| 次要色 | #ffd4d4 | 未激活进度点 |
| 文字主色 | #333333 | 标题、主要文字 |
| 文字次色 | #999999 | 说明文字 |

### 2. 页面结构

应用包含 3 个主要页面，通过底部导航切换：

#### 2.1 计时页（主页）

纵向布局，从上到下：

1. **模式标题** - 显示"专注时间"/"短休息"/"长休息"
2. **进度指示器** - 4 个圆点，显示当前番茄周期进度
3. **圆形计时器** - 视觉焦点，显示剩余时间和状态
4. **控制按钮** - 暂停/停止按钮，白色背景 + 红色图标
5. **今日统计卡片** - 显示今日番茄数和专注分钟数

#### 2.2 统计页

数字卡片式布局：

- 今日番茄数
- 本周番茄数
- 本月番茄数
- 总计番茄数
- 连续使用天数

#### 2.3 设置页

配置项列表：

- 专注时间（滑块或数字输入，1-120 分钟）
- 短休息时间（滑块或数字输入，1-120 分钟）
- 长休息时间（滑块或数字输入，1-120 分钟）
- 长休息触发（每 N 个番茄后）

### 3. 底部导航栏

| 图标 | 标签 | 页面 |
|------|------|------|
| 🍅 | 计时 | 计时页 |
| 📊 | 统计 | 统计页 |
| ⚙️ | 设置 | 设置页 |

选中状态使用主色 (#ff6b6b)，未选中使用次色 (#999999)

## 架构设计

### 1. 项目结构

```
app/
├── src/main/java/com/github/dgxz99/pomodoro/
│   ├── MainActivity.kt
│   ├── ui/
│   │   ├── theme/
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   ├── screens/
│   │   │   ├── TimerScreen.kt
│   │   │   ├── StatsScreen.kt
│   │   │   └── SettingsScreen.kt
│   │   ├── components/
│   │   │   ├── CircularTimer.kt
│   │   │   ├── ControlButtons.kt
│   │   │   ├── ProgressIndicator.kt
│   │   │   ├── StatCard.kt
│   │   │   └── BottomNavBar.kt
│   │   └── navigation/
│   │       └── NavGraph.kt
│   ├── data/
│   │   ├── local/
│   │   │   ├── PomodoroDatabase.kt
│   │   │   ├── PomodoroDao.kt
│   │   │   └── PomodoroRecord.kt
│   │   ├── repository/
│   │   │   └── PomodoroRepository.kt
│   │   └── preferences/
│   │       └── SettingsDataStore.kt
│   ├── domain/
│   │   ├── model/
│   │   │   ├── TimerState.kt
│   │   │   └── TimerMode.kt
│   │   └── usecase/
│   │       ├── StartTimerUseCase.kt
│   │       └── GetStatsUseCase.kt
│   ├── service/
│   │   └── TimerService.kt
│   └── viewmodel/
│       ├── TimerViewModel.kt
│       ├── StatsViewModel.kt
│       └── SettingsViewModel.kt
└── src/main/res/
    └── ...
```

### 2. 数据模型

#### PomodoroRecord（Room Entity）

```kotlin
@Entity(tableName = "pomodoro_records")
data class PomodoroRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val completedAt: Long,  // 完成时间戳
    val duration: Int       // 时长（分钟）
)
```

#### TimerState

```kotlin
data class TimerState(
    val mode: TimerMode,           // 当前模式
    val remainingSeconds: Int,     // 剩余秒数
    val isRunning: Boolean,        // 是否运行中
    val currentPomodoroIndex: Int  // 当前是第几个番茄（0-3）
)
```

#### TimerMode

```kotlin
enum class TimerMode {
    FOCUS,       // 专注时间
    SHORT_BREAK, // 短休息
    LONG_BREAK   // 长休息
}
```

### 3. 依赖项

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose")

// Room
implementation("androidx.room:room-runtime")
implementation("androidx.room:room-ktx")
kapt("androidx.room:room-compiler")

// DataStore
implementation("androidx.datastore:datastore-preferences")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
```

## 非功能需求

### 性能

- 计时器精度：误差不超过 1 秒
- 应用启动时间：< 2 秒

### 兼容性

- 支持 Android 7.0 (API 24) 及以上版本
- 支持深色/浅色模式（后续扩展）

### 数据安全

- 所有数据本地存储
- 无网络请求，无隐私数据收集
