package com.cacountdown.app.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint

/** Re-arms the daily refresh job and every student alarm after a reboot or device time change. */
@AndroidEntryPoint
class BootRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkScheduler.scheduleDailyRefresh(context)
        WorkManager.getInstance(context).enqueue(OneTimeWorkRequestBuilder<RescheduleAlarmsWorker>().build())
    }
}
