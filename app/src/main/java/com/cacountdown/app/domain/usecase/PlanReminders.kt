package com.cacountdown.app.domain.usecase

import com.cacountdown.app.domain.model.ReminderFrequencyStage
import com.cacountdown.app.domain.model.STANDARD_MILESTONES
import javax.inject.Inject

/**
 * Decides which milestone (if any) fires today, and which progress-reminder
 * frequency stage a subject is currently in. Firing each milestone once is
 * enforced by the caller checking `reminder_log` before sending — this class
 * only answers "what, if anything, is due", so it stays a pure function.
 */
class PlanReminders @Inject constructor() {

    /** The milestone that matches today's day-count, or null if none does. */
    fun dueMilestone(daysRemaining: Long): Int? =
        STANDARD_MILESTONES.map { it.daysBefore }.firstOrNull { it.toLong() == daysRemaining }

    fun frequencyStage(daysRemaining: Long): ReminderFrequencyStage = when {
        daysRemaining > 60 -> ReminderFrequencyStage.WEEKLY
        daysRemaining > 30 -> ReminderFrequencyStage.EVERY_3_DAYS
        daysRemaining > 14 -> ReminderFrequencyStage.TWICE_WEEKLY_STRONG
        daysRemaining > 7 -> ReminderFrequencyStage.DAILY_STATUS
        else -> ReminderFrequencyStage.DAILY_COUNTDOWN
    }
}
