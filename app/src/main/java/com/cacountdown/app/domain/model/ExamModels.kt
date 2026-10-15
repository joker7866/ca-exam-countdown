package com.cacountdown.app.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Which papers a student is sitting. */
enum class ExamGroup { GROUP_1, GROUP_2 }

/**
 * One paper's effective schedule: [exam_override] if the student edited it,
 * else the [exam_schedule] row from the official timetable. The repository
 * layer resolves this join (the `effective_exam` Room view); the domain
 * layer only ever sees the result.
 */
data class Exam(
    val subjectCode: String,
    val subjectName: String,
    val group: ExamGroup,
    val examDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    val examDateTime: LocalDateTime get() = LocalDateTime.of(examDate, startTime)
    val examEndDateTime: LocalDateTime get() = LocalDateTime.of(examDate, endTime)
}

/** The countdown state for one exam, computed fresh at read time — never stored. */
sealed class ExamState {
    data class DaysLeft(val days: Long) : ExamState()
    data object ExamToday : ExamState()
    data object Completed : ExamState()
}

data class ExamCountdown(
    val exam: Exam,
    val state: ExamState,
)

enum class UrgencyStatus { ON_TRACK, ATTENTION, BEHIND, URGENT }

/**
 * A subject's logged progress. `estimatedHoursTotal` is the total study-hours
 * budget for first-pass syllabus coverage (excludes revision and mock hours,
 * which are costed separately).
 */
data class SubjectProgress(
    val subjectCode: String,
    val totalChapters: Int,
    val chaptersDone: Int,
    val revisionsPlanned: Int,
    val revisionsDone: Int,
    val mocksPlanned: Int,
    val mocksDone: Int,
    val estimatedHoursTotal: Double,
    val missedSessionsThisWeek: Int = 0,
    val hoursLoggedLast14Days: Double = 0.0,
    /** Last time a topic was ticked off or a scheduled activity for this subject was completed; null if never. */
    val lastActivityAt: Instant? = null,
) {
    val syllabusFraction: Double
        get() = if (totalChapters == 0) 0.0 else chaptersDone.toDouble() / totalChapters

    val revisionFraction: Double
        get() = if (revisionsPlanned == 0) 0.0 else revisionsDone.toDouble() / revisionsPlanned

    val mocksBehindPlan: Boolean
        get() = mocksDone < mocksPlanned

    val revisionsLeft: Int get() = (revisionsPlanned - revisionsDone).coerceAtLeast(0)
    val mocksLeft: Int get() = (mocksPlanned - mocksDone).coerceAtLeast(0)
}

/** Tunable constants from the daily-target formula (spec §: Core logic, 3). */
data class DailyTargetConfig(
    val hoursPerRevision: Double = 4.0,
    val hoursPerMockTest: Double = 3.0,
    val roundToMinutes: Int = 5,
)

data class DailyTarget(
    val hoursLeft: Double,
    val studyDaysLeft: Int,
    val requiredMinutesPerDay: Int,
)

/** Tunable thresholds from the urgency table (spec §: Reminders and background work). */
data class UrgencyThresholds(
    val revisionBufferDays: Int = 14,
    val urgentDaysMax: Int = 30,
    val urgentSyllabusMax: Double = 0.60,
    val urgentCloseDays: Int = 7,
    val urgentCloseRevisionMax: Double = 0.50,
    val urgentPaceMultiplier: Double = 2.0,
    val behindSyllabusGapPoints: Double = 15.0,
    val behindPaceMultiplierMin: Double = 1.5,
    val attentionSyllabusGapPoints: Double = 5.0,
    val attentionMissedSessions: Int = 2,
    /** Days with no logged activity on a subject before Attention fires, even if the syllabus-gap maths looks fine. */
    val attentionNoActivityDays: Int = 3,
)

data class Milestone(val daysBefore: Int)

val STANDARD_MILESTONES = listOf(90, 60, 45, 30, 21, 14, 7, 3, 1, 0).map { Milestone(it) }

enum class ReminderFrequencyStage(val description: String) {
    WEEKLY("Weekly progress check"),
    EVERY_3_DAYS("Every 3 days"),
    TWICE_WEEKLY_STRONG("Twice a week, stronger wording"),
    DAILY_STATUS("Daily preparation status"),
    DAILY_COUNTDOWN("Daily countdown + top 3 revision priorities"),
}
