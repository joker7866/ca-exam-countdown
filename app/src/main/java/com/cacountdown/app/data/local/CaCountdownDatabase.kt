package com.cacountdown.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cacountdown.app.data.local.dao.EffectiveExamDao
import com.cacountdown.app.data.local.dao.ExamOverrideDao
import com.cacountdown.app.data.local.dao.ExamScheduleDao
import com.cacountdown.app.data.local.dao.PendingScheduleUpdateDao
import com.cacountdown.app.data.local.dao.ReminderLogDao
import com.cacountdown.app.data.local.dao.StudySessionDao
import com.cacountdown.app.data.local.dao.SubjectProgressDao
import com.cacountdown.app.data.local.dao.UserSetupDao
import com.cacountdown.app.data.local.entity.EffectiveExamView
import com.cacountdown.app.data.local.entity.ExamOverrideEntity
import com.cacountdown.app.data.local.entity.ExamScheduleEntity
import com.cacountdown.app.data.local.entity.PendingScheduleUpdateEntity
import com.cacountdown.app.data.local.entity.ReminderLogEntity
import com.cacountdown.app.data.local.entity.StudySessionEntity
import com.cacountdown.app.data.local.entity.SubjectProgressEntity
import com.cacountdown.app.data.local.entity.UserSetupEntity

@Database(
    entities = [
        ExamScheduleEntity::class,
        UserSetupEntity::class,
        ExamOverrideEntity::class,
        SubjectProgressEntity::class,
        StudySessionEntity::class,
        ReminderLogEntity::class,
        PendingScheduleUpdateEntity::class,
    ],
    views = [EffectiveExamView::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class CaCountdownDatabase : RoomDatabase() {
    abstract fun examScheduleDao(): ExamScheduleDao
    abstract fun effectiveExamDao(): EffectiveExamDao
    abstract fun userSetupDao(): UserSetupDao
    abstract fun examOverrideDao(): ExamOverrideDao
    abstract fun subjectProgressDao(): SubjectProgressDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun reminderLogDao(): ReminderLogDao
    abstract fun pendingScheduleUpdateDao(): PendingScheduleUpdateDao

    companion object {
        const val NAME = "ca_countdown.db"
    }
}
