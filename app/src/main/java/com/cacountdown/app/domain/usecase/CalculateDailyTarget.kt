package com.cacountdown.app.domain.usecase

import com.cacountdown.app.domain.model.DailyTarget
import com.cacountdown.app.domain.model.DailyTargetConfig
import com.cacountdown.app.domain.model.SubjectProgress
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Hours left = (1 − syllabus%) × estimated subject hours
 *            + revisions left × hours per revision
 *            + mock tests left × hours per mock
 * Study days left = days remaining − rest days the student chose
 * Required per day = hours left ÷ study days left, rounded to the nearest
 * [DailyTargetConfig.roundToMinutes].
 *
 * There is no separate "missed session" branch: a missed session never
 * advanced progress, so it never left `hoursLeft` — recomputing from the
 * latest [SubjectProgress] on the same day already reflects it, spreading
 * the shortfall over the days that remain.
 */
class CalculateDailyTarget @Inject constructor() {

    operator fun invoke(
        progress: SubjectProgress,
        daysRemaining: Long,
        restDaysChosen: Int = 0,
        config: DailyTargetConfig = DailyTargetConfig(),
    ): DailyTarget {
        val hoursLeft =
            (1 - progress.syllabusFraction) * progress.estimatedHoursTotal +
                progress.revisionsLeft * config.hoursPerRevision +
                progress.mocksLeft * config.hoursPerMockTest

        val studyDaysLeft = (daysRemaining - restDaysChosen).coerceAtLeast(1).toInt()

        val rawMinutes = (hoursLeft / studyDaysLeft) * 60.0
        val roundTo = config.roundToMinutes
        val requiredMinutes = (rawMinutes / roundTo).roundToInt() * roundTo

        return DailyTarget(
            hoursLeft = hoursLeft,
            studyDaysLeft = studyDaysLeft,
            requiredMinutesPerDay = requiredMinutes.coerceAtLeast(0),
        )
    }
}
