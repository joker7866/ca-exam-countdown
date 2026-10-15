package com.cacountdown.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val STUDY_ALARMS = "study_alarms"
    const val MILESTONES = "milestones"
    const val PROGRESS_CHECKINS = "progress_checkins"

    fun createAll(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(
            NotificationChannel(STUDY_ALARMS, "Study alarms", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Alarms you set for study sessions" },
        )
        manager.createNotificationChannel(
            NotificationChannel(MILESTONES, "Milestone alerts", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "90, 60, 45 … 1 day, exam day" },
        )
        manager.createNotificationChannel(
            NotificationChannel(PROGRESS_CHECKINS, "Progress check-ins", NotificationManager.IMPORTANCE_DEFAULT)
                .apply { description = "Syllabus and revision progress nudges" },
        )
    }
}
