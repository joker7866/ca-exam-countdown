package com.cacountdown.app.domain

import com.cacountdown.app.domain.model.DailyTarget
import com.cacountdown.app.domain.model.SubjectProgress
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.domain.usecase.CalculateUrgency
import com.google.common.truth.Truth.assertThat
import org.junit.Test

private fun progress(
    syllabusFraction: Double = 0.5,
    revisionFraction: Double = 0.5,
    missedSessionsThisWeek: Int = 0,
    mocksBehind: Boolean = false,
    hoursLoggedLast14Days: Double = 14.0, // 1h/day actual pace by default
) = SubjectProgress(
    subjectCode = "P1",
    totalChapters = 100,
    chaptersDone = (syllabusFraction * 100).toInt(),
    revisionsPlanned = 100,
    revisionsDone = (revisionFraction * 100).toInt(),
    mocksPlanned = if (mocksBehind) 1 else 0,
    mocksDone = 0,
    estimatedHoursTotal = 100.0,
    missedSessionsThisWeek = missedSessionsThisWeek,
    hoursLoggedLast14Days = hoursLoggedLast14Days,
)

private fun target(hoursLeft: Double, studyDaysLeft: Int) =
    DailyTarget(hoursLeft = hoursLeft, studyDaysLeft = studyDaysLeft, requiredMinutesPerDay = 0)

class CalculateUrgencyTest {

    private val calculate = CalculateUrgency()

    @Test
    fun `urgent when close and syllabus under 60 percent`() {
        val result = calculate(
            progress = progress(syllabusFraction = 0.5, revisionFraction = 0.9),
            daysRemaining = 25, // <= urgentDaysMax (30)
            daysSinceSetup = 25,
            dailyTarget = target(hoursLeft = 1.0, studyDaysLeft = 25), // low pace, won't trip the pace rule
        )
        assertThat(result).isEqualTo(UrgencyStatus.URGENT)
    }

    @Test
    fun `urgent when days 7 or fewer and revision under 50 percent`() {
        val result = calculate(
            progress = progress(syllabusFraction = 0.9, revisionFraction = 0.3),
            daysRemaining = 5, // <= urgentCloseDays (7)
            daysSinceSetup = 80,
            dailyTarget = target(hoursLeft = 1.0, studyDaysLeft = 5),
        )
        assertThat(result).isEqualTo(UrgencyStatus.URGENT)
    }

    @Test
    fun `urgent when required pace exceeds twice the actual pace`() {
        val result = calculate(
            progress = progress(syllabusFraction = 0.95, revisionFraction = 0.95, hoursLoggedLast14Days = 7.0), // actual 0.5h/day
            daysRemaining = 50, // outside both days-based urgent rules
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 100.0, studyDaysLeft = 50), // required 2h/day > 2x 0.5h/day
        )
        assertThat(result).isEqualTo(UrgencyStatus.URGENT)
    }

    @Test
    fun `behind when syllabus is more than 15 points below expected`() {
        // daysSinceSetup=50, daysRemaining=50, revisionBuffer=14 -> expected = 50/86 = 58%
        val result = calculate(
            progress = progress(syllabusFraction = 0.30, revisionFraction = 0.9),
            daysRemaining = 50,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 50), // low required pace, stays out of Urgent
        )
        assertThat(result).isEqualTo(UrgencyStatus.BEHIND)
    }

    @Test
    fun `behind when required pace is 1point5 to 2x actual`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9, hoursLoggedLast14Days = 14.0), // actual 1h/day
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 90.0, studyDaysLeft = 50), // required 1.8h/day -> ratio 1.8
        )
        assertThat(result).isEqualTo(UrgencyStatus.BEHIND)
    }

    @Test
    fun `attention when syllabus is 5 to 15 points below expected`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected - 0.10, revisionFraction = 0.9),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
        )
        assertThat(result).isEqualTo(UrgencyStatus.ATTENTION)
    }

    @Test
    fun `attention when 2 or more sessions missed this week`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9, missedSessionsThisWeek = 2),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
        )
        assertThat(result).isEqualTo(UrgencyStatus.ATTENTION)
    }

    @Test
    fun `attention when mock tests are behind plan`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9, mocksBehind = true),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
        )
        assertThat(result).isEqualTo(UrgencyStatus.ATTENTION)
    }

    @Test
    fun `attention when no activity logged for the threshold number of days`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
            daysSinceLastActivity = 3, // default attentionNoActivityDays
        )
        assertThat(result).isEqualTo(UrgencyStatus.ATTENTION)
    }

    @Test
    fun `on track when last activity was recent, under the no-activity threshold`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
            daysSinceLastActivity = 2,
        )
        assertThat(result).isEqualTo(UrgencyStatus.ON_TRACK)
    }

    @Test
    fun `attention when a scheduled item for the subject is overdue`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
            hasOverdueScheduledItem = true,
        )
        assertThat(result).isEqualTo(UrgencyStatus.ATTENTION)
    }

    @Test
    fun `on track when everything is in line`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 50, daysRemaining = 60)
        val result = calculate(
            progress = progress(syllabusFraction = expected, revisionFraction = 0.9),
            daysRemaining = 60,
            daysSinceSetup = 50,
            dailyTarget = target(hoursLeft = 10.0, studyDaysLeft = 60),
        )
        assertThat(result).isEqualTo(UrgencyStatus.ON_TRACK)
    }

    @Test
    fun `expected syllabus is capped at 100 percent`() {
        val expected = calculate.expectedSyllabusFraction(daysSinceSetup = 1000, daysRemaining = 5)
        assertThat(expected).isAtMost(1.0)
    }
}
