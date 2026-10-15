package com.cacountdown.app.domain.usecase

import com.cacountdown.app.domain.model.DailyTarget
import com.cacountdown.app.domain.model.SubjectProgress
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.domain.model.UrgencyThresholds
import javax.inject.Inject

/**
 * First rule that matches wins — Urgent, then Behind schedule, then
 * Attention needed, else On track. Thresholds are starting values meant to
 * be tuned against real student data (spec §: Reminders and background
 * work), so every one of them is a parameter with a documented default
 * rather than a literal buried in the branches below.
 */
class CalculateUrgency @Inject constructor() {

    operator fun invoke(
        progress: SubjectProgress,
        daysRemaining: Long,
        daysSinceSetup: Long,
        dailyTarget: DailyTarget,
        daysSinceLastActivity: Long? = null,
        hasOverdueScheduledItem: Boolean = false,
        thresholds: UrgencyThresholds = UrgencyThresholds(),
    ): UrgencyStatus {
        val expectedSyllabus = expectedSyllabusFraction(daysSinceSetup, daysRemaining, thresholds)
        val syllabusGapPoints = (expectedSyllabus - progress.syllabusFraction) * 100.0

        val requiredPace = if (dailyTarget.studyDaysLeft > 0) {
            dailyTarget.hoursLeft / dailyTarget.studyDaysLeft
        } else {
            dailyTarget.hoursLeft
        }
        val actualPace = progress.hoursLoggedLast14Days / 14.0

        val isUrgent =
            (daysRemaining <= thresholds.urgentDaysMax && progress.syllabusFraction < thresholds.urgentSyllabusMax) ||
                (daysRemaining <= thresholds.urgentCloseDays && progress.revisionFraction < thresholds.urgentCloseRevisionMax) ||
                (actualPace > 0.0 && requiredPace > thresholds.urgentPaceMultiplier * actualPace)

        if (isUrgent) return UrgencyStatus.URGENT

        val paceRatio = if (actualPace > 0.0) requiredPace / actualPace else 0.0

        val isBehind =
            syllabusGapPoints > thresholds.behindSyllabusGapPoints ||
                (paceRatio in thresholds.behindPaceMultiplierMin..thresholds.urgentPaceMultiplier)

        if (isBehind) return UrgencyStatus.BEHIND

        val isAttention =
            syllabusGapPoints in thresholds.attentionSyllabusGapPoints..thresholds.behindSyllabusGapPoints ||
                progress.missedSessionsThisWeek >= thresholds.attentionMissedSessions ||
                progress.mocksBehindPlan ||
                (daysSinceLastActivity != null && daysSinceLastActivity >= thresholds.attentionNoActivityDays) ||
                hasOverdueScheduledItem

        if (isAttention) return UrgencyStatus.ATTENTION

        return UrgencyStatus.ON_TRACK
    }

    /** Expected syllabus % = days since setup ÷ (days since setup + days remaining − revision buffer), capped at 100%. */
    fun expectedSyllabusFraction(
        daysSinceSetup: Long,
        daysRemaining: Long,
        thresholds: UrgencyThresholds = UrgencyThresholds(),
    ): Double {
        val denominator = daysSinceSetup + daysRemaining - thresholds.revisionBufferDays
        if (denominator <= 0) return 1.0
        return (daysSinceSetup.toDouble() / denominator).coerceAtMost(1.0)
    }
}
