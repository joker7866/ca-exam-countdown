package com.cacountdown.app.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint

/** Re-arms the daily refresh job after a reboot or device time change (spec §: Reminders and background work). */
@AndroidEntryPoint
class BootRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        WorkScheduler.scheduleDailyRefresh(context)
    }
}
