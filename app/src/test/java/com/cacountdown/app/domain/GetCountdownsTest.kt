package com.cacountdown.app.domain

import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamGroup
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.usecase.GetCountdowns
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

private val KOLKATA = ZoneId.of("Asia/Kolkata")

private fun clockAt(date: LocalDate, time: LocalTime = LocalTime.of(9, 0), zone: ZoneId = KOLKATA): Clock =
    Clock.fixed(date.atTime(time).atZone(zone).toInstant(), zone)

private fun exam(
    date: LocalDate,
    start: LocalTime = LocalTime.of(14, 0),
    end: LocalTime = LocalTime.of(17, 0),
) = Exam(
    subjectCode = "P1",
    subjectName = "Advanced Accounting",
    group = ExamGroup.GROUP_1,
    examDate = date,
    startTime = start,
    endTime = end,
)

class GetCountdownsTest {

    @Test
    fun `91 days on 6 Oct 2026 for 5 Jan 2027`() {
        val clock = clockAt(LocalDate.of(2026, 10, 6))
        val result = GetCountdowns(clock).invoke(exam(LocalDate.of(2027, 1, 5)))

        assertThat(result.state).isEqualTo(ExamState.DaysLeft(91))
    }

    @Test
    fun `exam day before end time is ExamToday`() {
        val examDate = LocalDate.of(2027, 1, 5)
        val clock = clockAt(examDate, LocalTime.of(10, 0))
        val result = GetCountdowns(clock).invoke(exam(examDate, start = LocalTime.of(14, 0), end = LocalTime.of(17, 0)))

        assertThat(result.state).isEqualTo(ExamState.ExamToday)
    }

    @Test
    fun `exam day after end time is Completed`() {
        val examDate = LocalDate.of(2027, 1, 5)
        val clock = clockAt(examDate, LocalTime.of(18, 0))
        val result = GetCountdowns(clock).invoke(exam(examDate, start = LocalTime.of(14, 0), end = LocalTime.of(17, 0)))

        assertThat(result.state).isEqualTo(ExamState.Completed)
    }

    @Test
    fun `day after the exam is Completed`() {
        val clock = clockAt(LocalDate.of(2027, 1, 6))
        val result = GetCountdowns(clock).invoke(exam(LocalDate.of(2027, 1, 5)))

        assertThat(result.state).isEqualTo(ExamState.Completed)
    }

    @Test
    fun `leap year is counted correctly`() {
        // 2028 is a leap year: 29 Feb exists, so 29 Jan -> 1 Mar is 32 days, not 31.
        val clock = clockAt(LocalDate.of(2028, 1, 29))
        val result = GetCountdowns(clock).invoke(exam(LocalDate.of(2028, 3, 1)))

        assertThat(result.state).isEqualTo(ExamState.DaysLeft(32))
    }

    @Test
    fun `device in another timezone still counts Kolkata calendar days`() {
        // 2026-10-06 23:30 IST is already 2026-10-06 18:00 UTC; a device whose
        // *local* clock reads UTC must not see this as a different day.
        val istMidnightInUtc = LocalDate.of(2026, 10, 6).atTime(23, 30).atZone(KOLKATA).toInstant()
        val clock = Clock.fixed(istMidnightInUtc, ZoneOffset.UTC)

        val result = GetCountdowns(Clock.fixed(clock.instant(), KOLKATA)).invoke(exam(LocalDate.of(2027, 1, 5)))

        assertThat(result.state).isEqualTo(ExamState.DaysLeft(91))
    }

    @Test
    fun `negative days (exam far in the past) is Completed`() {
        val clock = clockAt(LocalDate.of(2027, 2, 1))
        val result = GetCountdowns(clock).invoke(exam(LocalDate.of(2027, 1, 5)))

        assertThat(result.state).isEqualTo(ExamState.Completed)
    }
}
