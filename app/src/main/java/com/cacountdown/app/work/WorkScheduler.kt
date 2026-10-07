package com.cacountdown.app.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

object WorkScheduler {

    private val KOLKATA = ZoneId.of("Asia/Kolkata")

    /** Schedules [DailyRefreshWorker] to run once every 24h, first firing just after the next midnight IST. */
    fun scheduleDailyRefresh(context: Context) {
        val now = LocalDateTime.now(KOLKATA)
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay().plusMinutes(1)
        val initialDelay = Duration.between(now, nextMidnight)

        val request = PeriodicWorkRequestBuilder<DailyRefreshWorker>(Duration.ofHours(24))
            .setInitialDelay(initialDelay)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyRefreshWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
