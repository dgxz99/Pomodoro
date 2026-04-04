# Pomodoro App - Phase 3: Stats, Settings & Notifications

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Complete stats page, settings page, and notification functionality

**Architecture:** MVVM, Foreground Service for background timing

**Tech Stack:** Jetpack Compose, WorkManager/Service, NotificationManager

---

## File Structure

```
app/src/main/java/com/github/dgxz99/pomodoro/
├── ui/
│   ├── components/
│   │   └── StatCard.kt       # 统计卡片组件
│   └── screens/
│       ├── StatsScreen.kt    # 统计页面
│       └── SettingsScreen.kt # 设置页面
├── viewmodel/
│   ├── StatsViewModel.kt     # 统计数据
│   └── SettingsViewModel.kt  # 设置管理
├── service/
│   └── TimerService.kt       # 前台服务
└── util/
    └── NotificationHelper.kt # 通知工具
```

---

## Task 1: StatsViewModel

**Files:** `viewmodel/StatsViewModel.kt`

**State:**
- `todayCount: Int`
- `weekCount: Int`
- `monthCount: Int`
- `totalCount: Int`
- `streakDays: Int`

**Logic:**
- Load counts from PomodoroRepository
- Calculate streak days (连续使用天数)

- [ ] Create StatsViewModel
- [ ] Implement streak calculation
- [ ] Commit: "feat: add StatsViewModel"

---

## Task 2: StatCard Component

**Files:** `ui/components/StatCard.kt`

**Props:**
- `title: String`
- `value: Int`
- `subtitle: String?`

**UI:**
- 白色卡片 + 阴影
- 大数字 (主色)
- 标签文字 (次色)

- [ ] Create StatCard composable
- [ ] Commit: "feat: add StatCard component"

---

## Task 3: StatsScreen

**Files:** `ui/screens/StatsScreen.kt`

**Layout:**
- 标题: "统计"
- Grid of StatCards:
  - 今日番茄
  - 本周番茄
  - 本月番茄
  - 总计番茄
  - 连续天数

- [ ] Create StatsScreen
- [ ] Connect to StatsViewModel
- [ ] Commit: "feat: implement StatsScreen"

---

## Task 4: SettingsViewModel

**Files:** `viewmodel/SettingsViewModel.kt`

**State:**
- `focusDuration: Int` (1-120)
- `shortBreakDuration: Int` (1-20)
- `longBreakDuration: Int` (1-60)
- `pomodorosUntilLongBreak: Int` (2-8)

**Actions:**
- `setFocusDuration(minutes: Int)`
- `setShortBreakDuration(minutes: Int)`
- `setLongBreakDuration(minutes: Int)`
- `setPomodorosUntilLongBreak(count: Int)`

- [ ] Create SettingsViewModel
- [ ] Implement validation (min/max constraints)
- [ ] Commit: "feat: add SettingsViewModel"

---

## Task 5: SettingsScreen

**Files:** `ui/screens/SettingsScreen.kt`

**Layout:**
- 标题: "设置"
- 设置项列表 (使用 Slider 或 NumberPicker):
  - 专注时间: 1-120 分钟
  - 短休息: 1-20 分钟
  - 长休息: 1-60 分钟
  - 长休息间隔: 2-8 个番茄

**UI:**
- 每项显示当前值
- Slider for adjustment
- 实时保存

- [ ] Create SettingsScreen
- [ ] Create setting item component with slider
- [ ] Connect to SettingsViewModel
- [ ] Commit: "feat: implement SettingsScreen"

---

## Task 6: Notification Helper

**Files:** `util/NotificationHelper.kt`

**Functions:**
- `createNotificationChannel(context)`
- `showTimerCompleteNotification(context, mode: TimerMode)`

**Requirements:**
- 使用系统默认提示音
- 通知内容:
  - FOCUS 完成: "专注时间结束，休息一下吧！"
  - SHORT_BREAK 完成: "休息结束，继续专注！"
  - LONG_BREAK 完成: "长休息结束，开始新的番茄周期！"

- [ ] Create NotificationHelper
- [ ] Create notification channel
- [ ] Implement notification display
- [ ] Commit: "feat: add notification support"

---

## Task 7: Timer Foreground Service

**Files:** `service/TimerService.kt`

**Purpose:**
- 后台运行时保持计时准确
- 显示持续通知 (ongoing notification)

**Implementation:**
- Extend `Service`
- Start as foreground service
- Communicate with ViewModel via Binder or broadcast

**AndroidManifest:**
- Add `<service>` declaration
- Add `FOREGROUND_SERVICE` permission

- [ ] Create TimerService
- [ ] Implement foreground notification
- [ ] Update AndroidManifest
- [ ] Integrate with TimerViewModel
- [ ] Commit: "feat: add foreground service for background timing"

---

## Task 8: Final Integration

- [ ] Update NavGraph with all screens
- [ ] Verify all navigation works
- [ ] Test notification on timer complete
- [ ] Test background timing
- [ ] Test settings persistence
- [ ] Commit: "chore: phase 3 complete - full app functionality"

---

## Phase 3 Complete Checklist

- [ ] StatsScreen shows all statistics
- [ ] SettingsScreen allows configuration
- [ ] Settings persist across app restart
- [ ] Notification plays on timer complete
- [ ] Timer continues in background
- [ ] All navigation works smoothly
- [ ] App is fully functional
