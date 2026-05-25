# TodoAI — Android Todo App with AI Agents

> Feature-complete todo app for Android, built to teach AI agent fundamentals.

## What it does

| Tier | Features |
|---|---|
| **V1 — CRUD** | Create / read / update / delete tasks |
| **V2 — Organisation** | Priority badges (Low/Med/High), categories, filter + sort chips |
| **V3 — Dates** | Parsed due-date from natural language, WorkManager reminder receiver stub |
| **V4 — Polish** | Material 3 theme, search bar, exported summaries |
| **A1 — NLP Parser** | `!high #groceries tomorrow` → structured task, 100 % offline |
| **A2 — Priority Agent** | Keyword matcher (`urgent` → HIGH, `later` → LOW) |
| **A3 — Scheduler Agent** | Ranks open tasks by priority × deadline and prints a suggested day-plan |
| **A4 — Summary Agent** | Reads completed tasks from DB and produces a daily productivity blurb |
| **A5 — Voice Input** | `SpeechRecognizer` → free-text → NLP parser |

## Architecture

```
ui/                ← Jetpack Compose screens (no DB imports)
  ├─ ViewModel     ← StateFlow, orchestrates use-cases
  ├─ components    ← TaskCard, PriorityBadge, ScheduledTaskItem
  └─ theme         ← Material 3

domain/
  ├─ model         ← Task, Priority, Recurrence (plain Kotlin)
  └─ usecase/      ← Interfaces: AddTask, DeleteTask, ToggleComplete, Filter, …

data/
  ├─ local/        ← Room [TaskEntity], @Dao
  └─ repository/   ← TaskRepositoryImpl (offline-first)

agent/
  ├─ TaskParserAgent --- heuristic NLP (regex)
  ├─ tools/        ← Tool interface, AgentRunner, PriorityAgent,
  │                 SchedulerAgent, SummaryAgent
  └─ ParsedTask    ← output DTO

di/                ← Hilt modules (provides DB, repo, use-cases, tools)
util/              ← DateUtils, TaskReminderReceiver, VoiceInputHelper
```

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 1.9 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt 2.48 |
| Local DB | Room 2.6 + KSP |
| Agent infra | DIY (Tool / ToolCall / Observation loop) |

## How to build

1. Open the `TodoAI/` folder in Android Studio Giraffe+
2. Wait for Gradle sync
3. Click **Run** (minSdk 26, targetSdk 34)

## Key learning mappings

| Learning goal | Where to look |
|---|---|
| Prompt engineering | `TaskParserAgent.kt` pattern → output schema |
| Tool use | `Tool` interface — `/app/src/main/java/…/tools/` |
| Agent orchestration | `ScheduleViewModel` chains PriorityAgent → SchedulerAgent |
| State summarisation | `SummaryAgent.kt` + `SummaryViewModel` |
| Voice → NLU | `VoiceInputHelper.kt` + `TaskParserAgent` |

## License

MIT — built for learning.
