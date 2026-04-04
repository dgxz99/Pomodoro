# Pomodoro App - Phase 2: Timer & UI

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Implement timer functionality and main UI screens

**Architecture:** MVVM with Compose, CountDownTimer for timing logic

**Tech Stack:** Jetpack Compose, ViewModel, Navigation Compose

---

## File Structure

```
app/src/main/java/com/github/dgxz99/pomodoro/
├── ui/
│   ├── theme/
│   │   ├── Color.kt          # 主题颜色定义
│   │   ├── Theme.kt          # Material3 主题
│   │   └── Type.kt           # 排版定义
│   ├── components/
│   │   ├── CircularTimer.kt  # 圆形计时器组件
│   │   ├── ControlButtons.kt # 暂停/停止按钮
│   │   ├── ProgressIndicator.kt # 番茄进度指示器
│   │   └── BottomNavBar.kt   # 底部导航栏
│   ├── screens/
│   │   └── TimerScreen.kt    # 计时主页面
│   └── navigation/
│       └── NavGraph.kt       # 导航图
├── viewmodel/
│   └── TimerViewModel.kt     # 计时逻辑
└── MainActivity.kt           # 入口
```

---

## Task 1: Theme Configuration

**Files:** `ui/theme/Color.kt`, `ui/theme/Theme.kt`, `ui/theme/Type.kt`

**Requirements:**
- 主色: #ff6b6b (番茄红)
- 背景色: #fef9f3 (暖白色)
- 卡片色: #ffffff
- 次要色: #ffd4d4 (未激活进度点)
- 文字主色: #333333
- 文字次色: #999999

- [ ] Define color palette in Color.kt
- [ ] Configure Material3 theme with custom colors
- [ ] Define typography styles
- [ ] Commit: "feat: configure app theme with warm tomato style"

---

## Task 2: TimerViewModel

**Files:** `viewmodel/TimerViewModel.kt`

**State:**
- `timerState: TimerState` (mode, remainingSeconds, isRunning, currentPomodoroIndex)
- `settings` from DataStore (focusDuration, shortBreakDuration, longBreakDuration, pomodorosUntilLongBreak)

**Actions:**
- `start()` - Start/resume timer
- `pause()` - Pause timer
- `stop()` - Stop and reset (don't record)
- `onTimerComplete()` - Handle completion, switch mode, record if FOCUS completed

**Implementation Notes:**
- Use `CountDownTimer` or coroutine-based timer
- Read settings from SettingsDataStore
- Insert record via PomodoroRepository when FOCUS completes
- Auto-transition: FOCUS → SHORT_BREAK (or LONG_BREAK after N pomodoros)

- [ ] Create TimerViewModel with state management
- [ ] Implement timer countdown logic
- [ ] Handle mode transitions
- [ ] Integrate with Repository and DataStore
- [ ] Commit: "feat: implement TimerViewModel with countdown logic"

---

## Task 3: UI Components

### 3.1 CircularTimer

**Files:** `ui/components/CircularTimer.kt`

**Props:**
- `remainingSeconds: Int`
- `totalSeconds: Int`
- `mode: TimerMode`

**UI:**
- 圆形背景 (#ff6b6b)
- 时间显示 (MM:SS 格式)
- 状态文字 (专注中/休息中)
- Optional: 进度弧线

- [ ] Create CircularTimer composable
- [ ] Format time as MM:SS
- [ ] Apply theme colors based on mode

### 3.2 ControlButtons

**Files:** `ui/components/ControlButtons.kt`

**Props:**
- `isRunning: Boolean`
- `onStart: () -> Unit`
- `onPause: () -> Unit`
- `onStop: () -> Unit`

**UI:**
- 白色圆形按钮 + 红色图标
- 显示: 开始 | 暂停 + 停止 (based on state)

- [ ] Create ControlButtons composable
- [ ] Conditional rendering based on isRunning

### 3.3 ProgressIndicator

**Files:** `ui/components/ProgressIndicator.kt`

**Props:**
- `currentIndex: Int`
- `total: Int`

**UI:**
- N 个圆点，已完成用主色，未完成用次要色

- [ ] Create ProgressIndicator composable
- [ ] Dynamic dot count based on settings

### 3.4 BottomNavBar

**Files:** `ui/components/BottomNavBar.kt`

**Items:**
- 计时 (🍅) - TimerScreen
- 统计 (📊) - StatsScreen
- 设置 (⚙️) - SettingsScreen

- [ ] Create BottomNavBar composable
- [ ] Integrate with Navigation

- [ ] Commit: "feat: add UI components for timer screen"

---

## Task 4: TimerScreen

**Files:** `ui/screens/TimerScreen.kt`

**Layout (top to bottom):**
1. Mode title (专注时间/短休息/长休息)
2. ProgressIndicator
3. CircularTimer
4. ControlButtons
5. Today stats card (今日番茄数 + 专注分钟)

- [ ] Create TimerScreen composable
- [ ] Connect to TimerViewModel
- [ ] Display today's stats from Repository
- [ ] Commit: "feat: implement TimerScreen layout"

---

## Task 5: Navigation Setup

**Files:** `ui/navigation/NavGraph.kt`, `MainActivity.kt`

**Routes:**
- `timer` - TimerScreen (default)
- `stats` - StatsScreen (placeholder)
- `settings` - SettingsScreen (placeholder)

- [ ] Create NavGraph with bottom navigation
- [ ] Update MainActivity with Compose content
- [ ] Commit: "feat: setup navigation with bottom nav bar"

---

## Task 6: Integration & Testing

- [ ] Run app on emulator/device
- [ ] Verify timer countdown works
- [ ] Verify mode transitions
- [ ] Verify stats recording
- [ ] Commit: "chore: phase 2 complete - timer functionality"

---

## Phase 2 Complete Checklist

- [ ] Theme configured
- [ ] TimerViewModel functional
- [ ] All UI components created
- [ ] TimerScreen displays correctly
- [ ] Navigation working
- [ ] Timer countdown accurate
- [ ] Mode transitions work
- [ ] Records saved to database
