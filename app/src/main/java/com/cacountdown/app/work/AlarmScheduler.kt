package com.cacountdown.app.work

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.cacountdown.app.data.local.entity.AlarmEntity
import com.cacountdown.app.notifications.AlarmReceiver
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Schedules a student-set [AlarmEntity] through [AlarmManager]. Falls back to
 * an inexact alarm if exact-alarm permission has been denied (Android 12+),
 * same mitigation the spec calls out for the exam-day alerts.
 */
object AlarmScheduler {

    private val KOLKATA = ZoneId.of("Asia/Kolkata")

    fun schedule(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val triggerAt = nextTrigger(alarm)
        val pendingIntent = pendingIntentFor(context, alarm.id)

        val canScheduleExact = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    fun cancel(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        alarmManager.cancel(pendingIntentFor(context, alarmId))
    }

    private fun pendingIntentFor(context: Context, alarmId: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun nextTrigger(alarm: AlarmEntity): Long {
        val now = ZonedDateTime.now(KOLKATA)
        var candidate = LocalDateTime.of(now.toLocalDate(), java.time.LocalTime.of(alarm.hour, alarm.minute)).atZone(KOLKATA)
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(1)
        }
        return candidate.toInstant().toEpochMilli()
    }
}
