package com.cacountdown.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cacountdown.app.data.local.dao.AlarmDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/** Re-arms every enabled student alarm after a reboot, since AlarmManager entries don't survive one. */
@HiltWorker
class RescheduleAlarmsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val alarmDao: AlarmDao,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        alarmDao.allEnabled().forEach { alarm -> AlarmScheduler.schedule(applicationContext, alarm) }
        return Result.success()
    }
}
