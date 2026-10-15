<h1 align="center">🎯 CA Exam Countdown</h1>

<p align="center"><b>A smart countdown and study planner for ICAI CA Intermediate students — know exactly how much to study every day.</b></p>

<p align="center">
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/> <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/> <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/> <img src="https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white" alt="Material 3"/> <img src="https://img.shields.io/badge/Hilt-FF6F00?style=for-the-badge&logo=google&logoColor=white" alt="Hilt"/> <img src="https://img.shields.io/badge/Room%20DB-4CAF50?style=for-the-badge&logo=sqlite&logoColor=white" alt="Room DB"/> <img src="https://img.shields.io/badge/WorkManager-4285F4?style=for-the-badge&logo=android&logoColor=white" alt="WorkManager"/>
</p>

---

## 📖 About

CA students juggle many papers with fixed exam dates. **CA Exam Countdown** turns the official ICAI timetable and syllabus into a personal plan: it counts down to every paper, tracks chapter-by-chapter progress, calculates a realistic **daily study target**, and warns you early when a subject starts slipping behind.

Everything runs offline on the device — no account, no server.

## ✨ Features

- **Exam countdown** — days left for every paper (Group 1 / Group 2), with "Exam today" and "Completed" states, based on the bundled official timetable (editable if dates change)
- **Official syllabus checklist** — real ICAI Intermediate (New Scheme) chapter lists per paper; tick topics off as you finish them
- **Daily study target** — required minutes per day, recalculated from your remaining syllabus, revisions and mock tests
- **Urgency engine** — each subject is rated **On track / Attention / Behind / Urgent** from syllabus gap, study pace, missed sessions and inactivity
- **Smart reminders** — milestone alerts (90, 60, 45, 30, 21, 14, 7, 3, 1 days before) and reminder frequency that intensifies as exams approach
- **Study schedule & timetable** — plan sessions, set alarms, and keep goals
- **Analysis** — charts of progress and hours logged per subject
- **Notes & exam-day checklist**

## 🧠 How the core logic works

**Daily target**

```
Hours left       = (1 − syllabus%) × estimated subject hours
                 + revisions left × hours per revision (4h)
                 + mock tests left × hours per mock (3h)
Study days left  = days remaining − rest days chosen
Required per day = hours left ÷ study days left   (rounded to 5 min)
```

A missed session never advanced progress, so the shortfall is automatically spread over the remaining days.

**Urgency status** — first rule that matches wins:

| Status | Triggered when |
|---|---|
| 🔴 **Urgent** | ≤ 30 days left and syllabus < 60%, or ≤ 7 days left and revision < 50%, or required pace > 2× actual pace |
| 🟠 **Behind** | Syllabus more than 15 points behind the expected curve, or required pace 1.5–2× actual pace |
| 🟡 **Attention** | 5–15 points behind, ≥ 2 missed sessions this week, mocks behind plan, 3+ days with no activity, or an overdue scheduled item |
| 🟢 **On track** | None of the above |

All thresholds are named parameters with documented defaults (`UrgencyThresholds`), so they can be tuned against real student data.

## 🏗️ Architecture

MVVM with clean-architecture layers:

```
app/src/main/java/com/cacountdown/app/
  domain/
    model/        Exam, SubjectProgress, DailyTarget, UrgencyThresholds …
    usecase/      CalculateDailyTarget, CalculateUrgency, GetCountdowns, GetNextExam, PlanReminders
  data/
    local/        Room database, DAOs, entities, effective_exam view (official schedule + user overrides)
    repository/   ExamRepository, ProgressRepository, OfficialSyllabus
  di/             Hilt modules
  notifications/  Alarm receiver, notification channels
  work/           WorkManager daily refresh, alarm (re)scheduling, boot receiver
  ui/             Jetpack Compose screens — home, subjects, plan, topics, schedule,
                  timetable, alarms, goals, analysis, notes, checklist, alerts, settings
app/src/main/assets/
  timetable_inter_2027-01.json   bundled official exam timetable
```

## 🛠️ Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM + use cases (pure, unit-tested domain layer) |
| Storage | Room (with a SQL view for effective schedules) |
| DI | Hilt |
| Background work | WorkManager + exact alarms, reschedule on boot |
| Serialization | kotlinx-serialization (timetable JSON) |

`minSdk 26` · `targetSdk 36` · `compileSdk 36`

## 🚀 Build & run

Requirements: JDK 17+, Android SDK (platform 36).

```bash
./gradlew assembleDebug          # build debug APK
./gradlew installDebug           # install on a connected device / emulator
./gradlew testDebugUnitTest      # run unit tests
```

Or open the folder in Android Studio and press **Run**.

## ✅ Tests

JVM unit tests cover the core domain logic:

- `CalculateDailyTargetTest` — daily target maths, rounding, rest days
- `CalculateUrgencyTest` — every urgency rule and its boundaries
- `GetCountdownsTest` / `GetNextExamTest` — countdown states and next-exam selection
- `PlanRemindersTest` — milestone reminder planning

## 🗺️ Roadmap

- Foundation and Final level timetables and syllabi
- Study-hours logging widgets
- Cloud backup / multi-device sync


---

## 👨‍💻 Author

**Kishore** — B.Tech Computer Science · Software developer building practical apps for everyday problems in Tamil Nadu and India.

- GitHub: [@joker7866](https://github.com/joker7866)

<p align="center"><sub>⭐ If you find this project useful, consider giving it a star!</sub></p>
