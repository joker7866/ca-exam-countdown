package com.cacountdown.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cacountdown.app.data.repository.ExamRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Runs just after midnight IST, on app open, and on boot/time-change (spec
 * §: Reminders and background work). There is nothing to *write* for the
 * countdown itself — [com.cacountdown.app.domain.usecase.GetCountdowns]
 * recomputes it from the device clock on every read — so today this worker
 * only has to exist and succeed; it becomes the hook that ReminderWorker and
 * widget updates chain off once those land.
 */
@HiltWorker
class DailyRefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val examRepository: ExamRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val setup = examRepository.observeUserSetup().first() ?: return Result.success()
        examRepository.observeExamsForAttempt(setup.attempt).first()
        return Result.success()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "daily_refresh"
    }
}
