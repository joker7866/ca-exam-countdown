package com.cacountdown.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cacountdown.app.data.local.dao.AlarmDao
import com.cacountdown.app.data.local.dao.ChapterDao
import com.cacountdown.app.data.local.dao.ChecklistDao
import com.cacountdown.app.data.local.dao.EffectiveExamDao
import com.cacountdown.app.data.local.dao.ExamOverrideDao
import com.cacountdown.app.data.local.dao.ExamScheduleDao
import com.cacountdown.app.data.local.dao.GoalDao
import com.cacountdown.app.data.local.dao.NoteDao
import com.cacountdown.app.data.local.dao.PendingScheduleUpdateDao
import com.cacountdown.app.data.local.dao.PlannedActivityDao
import com.cacountdown.app.data.local.dao.ReminderLogDao
import com.cacountdown.app.data.local.dao.StudySessionDao
import com.cacountdown.app.data.local.dao.SubjectProgressDao
import com.cacountdown.app.data.local.dao.TimetableDao
import com.cacountdown.app.data.local.dao.UserSetupDao
import com.cacountdown.app.data.local.entity.AlarmEntity
import com.cacountdown.app.data.local.entity.ChapterEntity
import com.cacountdown.app.data.local.entity.ChecklistItemEntity
import com.cacountdown.app.data.local.entity.EffectiveExamView
import com.cacountdown.app.data.local.entity.ExamOverrideEntity
import com.cacountdown.app.data.local.entity.ExamScheduleEntity
import com.cacountdown.app.data.local.entity.GoalEntity
import com.cacountdown.app.data.local.entity.NoteEntity
import com.cacountdown.app.data.local.entity.PendingScheduleUpdateEntity
import com.cacountdown.app.data.local.entity.PlannedActivityEntity
import com.cacountdown.app.data.local.entity.ReminderLogEntity
import com.cacountdown.app.data.local.entity.StudySessionEntity
import com.cacountdown.app.data.local.entity.SubjectProgressEntity
import com.cacountdown.app.data.local.entity.TimetableEntryEntity
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
        NoteEntity::class,
        ChecklistItemEntity::class,
        TimetableEntryEntity::class,
        AlarmEntity::class,
        GoalEntity::class,
        ChapterEntity::class,
        PlannedActivityEntity::class,
    ],
    views = [EffectiveExamView::class],
    version = 4,
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
    abstract fun noteDao(): NoteDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun timetableDao(): TimetableDao
    abstract fun alarmDao(): AlarmDao
    abstract fun goalDao(): GoalDao
    abstract fun chapterDao(): ChapterDao
    abstract fun plannedActivityDao(): PlannedActivityDao

    companion object {
        const val NAME = "ca_countdown.db"
    }
}
