package com.cacountdown.app.domain

import com.cacountdown.app.domain.model.DailyTargetConfig
import com.cacountdown.app.domain.model.SubjectProgress
import com.cacountdown.app.domain.usecase.CalculateDailyTarget
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CalculateDailyTargetTest {

    private val calculate = CalculateDailyTarget()

    /** Spec worked example: 76 h ÷ 91 days = 0.84 h/day → shown as 50 min/day. */
    @Test
    fun `worked example from the spec`() {
        val progress = SubjectProgress(
            subjectCode = "P1",
            totalChapters = 1,
            chaptersDone = 0,
            revisionsPlanned = 0,
            revisionsDone = 0,
            mocksPlanned = 0,
            mocksDone = 0,
            estimatedHoursTotal = 76.0,
        )

        val target = calculate(progress, daysRemaining = 91)

        assertThat(target.hoursLeft).isWithin(0.001).of(76.0)
        assertThat(target.requiredMinutesPerDay).isEqualTo(50)
    }

    @Test
    fun `syllabus already done only counts revisions and mocks`() {
        val progress = SubjectProgress(
            subjectCode = "P1",
            totalChapters = 10,
            chaptersDone = 10,
            revisionsPlanned = 2,
            revisionsDone = 0,
            mocksPlanned = 1,
            mocksDone = 0,
            estimatedHoursTotal = 100.0,
        )
        // hours left = 0 (syllabus done) + 2*4 + 1*3 = 11h
        val target = calculate(progress, daysRemaining = 22, config = DailyTargetConfig(hoursPerRevision = 4.0, hoursPerMockTest = 3.0))

        assertThat(target.hoursLeft).isWithin(0.001).of(11.0)
        // 11h / 22 days = 0.5h/day = 30 min/day
        assertThat(target.requiredMinutesPerDay).isEqualTo(30)
    }

    @Test
    fun `rest days chosen shrink the study days left`() {
        val progress = SubjectProgress(
            subjectCode = "P1",
            totalChapters = 1,
            chaptersDone = 0,
            revisionsPlanned = 0,
            revisionsDone = 0,
            mocksPlanned = 0,
            mocksDone = 0,
            estimatedHoursTotal = 20.0,
        )

        val target = calculate(progress, daysRemaining = 20, restDaysChosen = 10)

        assertThat(target.studyDaysLeft).isEqualTo(10)
        // 20h / 10 days = 2h/day = 120 min/day
        assertThat(target.requiredMinutesPerDay).isEqualTo(120)
    }

    @Test
    fun `a missed session raises tomorrow's target because progress did not advance`() {
        val progress = SubjectProgress(
            subjectCode = "P1",
            totalChapters = 10,
            chaptersDone = 2,
            revisionsPlanned = 0,
            revisionsDone = 0,
            mocksPlanned = 0,
            mocksDone = 0,
            estimatedHoursTotal = 100.0,
        )

        // Day 1: 10 days left, target computed.
        val day1 = calculate(progress, daysRemaining = 10)
        // Day 2: student skipped yesterday's session, so progress (and hoursLeft) is unchanged,
        // but only 9 study days remain — the same hours must now fit in fewer days.
        val day2 = calculate(progress, daysRemaining = 9)

        assertThat(day2.hoursLeft).isEqualTo(day1.hoursLeft)
        assertThat(day2.requiredMinutesPerDay).isGreaterThan(day1.requiredMinutesPerDay)
    }

    @Test
    fun `study days left never drops below one even if rest days exceed days remaining`() {
        val progress = SubjectProgress(
            subjectCode = "P1",
            totalChapters = 1,
            chaptersDone = 0,
            revisionsPlanned = 0,
            revisionsDone = 0,
            mocksPlanned = 0,
            mocksDone = 0,
            estimatedHoursTotal = 10.0,
        )

        val target = calculate(progress, daysRemaining = 3, restDaysChosen = 5)

        assertThat(target.studyDaysLeft).isEqualTo(1)
    }

    @Test
    fun `rounds to the nearest 5 minutes`() {
        val progress = SubjectProgress(
            subjectCode = "P1",
            totalChapters = 1,
            chaptersDone = 0,
            revisionsPlanned = 0,
            revisionsDone = 0,
            mocksPlanned = 0,
            mocksDone = 0,
            estimatedHoursTotal = 1.0,
        )
        // 1h / 7 days = 8.57 min/day -> rounds to 10
        val target = calculate(progress, daysRemaining = 7)

        assertThat(target.requiredMinutesPerDay).isEqualTo(10)
    }
}
