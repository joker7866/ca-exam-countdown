package com.cacountdown.app.domain

import com.cacountdown.app.domain.model.ReminderFrequencyStage
import com.cacountdown.app.domain.usecase.PlanReminders
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PlanRemindersTest {

    private val plan = PlanReminders()

    @Test
    fun `each milestone fires exactly once, on its own day`() {
        val milestoneDays = listOf(90, 60, 45, 30, 21, 14, 7, 3, 1, 0)

        for (days in milestoneDays) {
            assertThat(plan.dueMilestone(days.toLong())).isEqualTo(days)
        }
    }

    @Test
    fun `a non-milestone day fires nothing`() {
        assertThat(plan.dueMilestone(89)).isNull()
        assertThat(plan.dueMilestone(50)).isNull()
        assertThat(plan.dueMilestone(2)).isNull()
    }

    @Test
    fun `frequency stage follows the table by days remaining`() {
        assertThat(plan.frequencyStage(61)).isEqualTo(ReminderFrequencyStage.WEEKLY)
        assertThat(plan.frequencyStage(60)).isEqualTo(ReminderFrequencyStage.EVERY_3_DAYS)
        assertThat(plan.frequencyStage(31)).isEqualTo(ReminderFrequencyStage.EVERY_3_DAYS)
        assertThat(plan.frequencyStage(30)).isEqualTo(ReminderFrequencyStage.TWICE_WEEKLY_STRONG)
        assertThat(plan.frequencyStage(15)).isEqualTo(ReminderFrequencyStage.TWICE_WEEKLY_STRONG)
        assertThat(plan.frequencyStage(14)).isEqualTo(ReminderFrequencyStage.DAILY_STATUS)
        assertThat(plan.frequencyStage(8)).isEqualTo(ReminderFrequencyStage.DAILY_STATUS)
        assertThat(plan.frequencyStage(7)).isEqualTo(ReminderFrequencyStage.DAILY_COUNTDOWN)
        assertThat(plan.frequencyStage(0)).isEqualTo(ReminderFrequencyStage.DAILY_COUNTDOWN)
    }
}
