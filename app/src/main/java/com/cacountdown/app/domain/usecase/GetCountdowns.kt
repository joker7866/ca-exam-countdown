package com.cacountdown.app.domain.usecase

import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamCountdown
import com.cacountdown.app.domain.model.ExamState
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Computes the countdown for one or many exams. Days remaining is always
 * derived from the device clock at read time — it is never stored as a
 * number, so Home, notifications and widgets can never disagree.
 *
 * @param clock injected so tests can pin "today" instead of depending on the
 * real system clock; production code gets `Clock.system(ZoneId.of("Asia/Kolkata"))`.
 */
class GetCountdowns @Inject constructor(private val clock: Clock) {

    operator fun invoke(exam: Exam): ExamCountdown {
        val now = LocalDateTime.now(clock)
        val today = now.toLocalDate()
        val days = ChronoUnit.DAYS.between(today, exam.examDate)

        val state = when {
            days < 0 -> ExamState.Completed
            days == 0L && now.isAfter(exam.examEndDateTime) -> ExamState.Completed
            days == 0L -> ExamState.ExamToday
            else -> ExamState.DaysLeft(days)
        }
        return ExamCountdown(exam, state)
    }

    operator fun invoke(exams: List<Exam>): List<ExamCountdown> = exams.map { invoke(it) }
}
