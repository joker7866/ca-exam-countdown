package com.cacountdown.app.domain

import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamGroup
import com.cacountdown.app.domain.usecase.GetCountdowns
import com.cacountdown.app.domain.usecase.GetNextExam
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

private val KOLKATA = ZoneId.of("Asia/Kolkata")

private fun clockAt(date: LocalDate): Clock =
    Clock.fixed(date.atTime(9, 0).atZone(KOLKATA).toInstant(), KOLKATA)

private fun exam(code: String, date: LocalDate, start: LocalTime = LocalTime.of(14, 0)) = Exam(
    subjectCode = code,
    subjectName = code,
    group = ExamGroup.GROUP_1,
    examDate = date,
    startTime = start,
    endTime = start.plusHours(3),
)

class GetNextExamTest {

    private val sixPapers = listOf(
        exam("P1", LocalDate.of(2027, 1, 5)),
        exam("P2", LocalDate.of(2027, 1, 7)),
        exam("P3", LocalDate.of(2027, 1, 9)),
        exam("P4", LocalDate.of(2027, 1, 13)),
        exam("P5", LocalDate.of(2027, 1, 15)),
        exam("P6", LocalDate.of(2027, 1, 17)),
    )

    @Test
    fun `each exam passes in turn as the attempt progresses`() {
        fun nextSubjectCode(today: LocalDate): String? {
            val useCase = GetNextExam(GetCountdowns(clockAt(today)))
            return useCase(sixPapers)?.exam?.subjectCode
        }

        assertThat(nextSubjectCode(LocalDate.of(2026, 10, 6))).isEqualTo("P1")
        assertThat(nextSubjectCode(LocalDate.of(2027, 1, 6))).isEqualTo("P2")
        assertThat(nextSubjectCode(LocalDate.of(2027, 1, 8))).isEqualTo("P3")
        assertThat(nextSubjectCode(LocalDate.of(2027, 1, 14))).isEqualTo("P5")
        assertThat(nextSubjectCode(LocalDate.of(2027, 1, 16))).isEqualTo("P6")
    }

    @Test
    fun `all exams done means no next exam, attempt is complete`() {
        val useCase = GetNextExam(GetCountdowns(clockAt(LocalDate.of(2027, 1, 18))))

        assertThat(useCase(sixPapers)).isNull()
        assertThat(useCase.isAttemptComplete(sixPapers)).isTrue()
    }

    @Test
    fun `an edited date earlier than official moves that exam up`() {
        val edited = listOf(
            exam("P1", LocalDate.of(2027, 1, 5)),
            exam("P2", LocalDate.of(2027, 1, 3)), // student moved this earlier than P1
        )
        val useCase = GetNextExam(GetCountdowns(clockAt(LocalDate.of(2026, 12, 1))))

        assertThat(useCase(edited)?.exam?.subjectCode).isEqualTo("P2")
    }

    @Test
    fun `empty exam list is not a completed attempt`() {
        val useCase = GetNextExam(GetCountdowns(clockAt(LocalDate.of(2026, 12, 1))))

        assertThat(useCase.isAttemptComplete(emptyList())).isFalse()
    }
}
