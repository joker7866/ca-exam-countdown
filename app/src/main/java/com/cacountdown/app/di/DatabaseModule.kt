package com.cacountdown.app.di

import android.content.Context
import androidx.room.Room
import com.cacountdown.app.data.local.CaCountdownDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CaCountdownDatabase =
        Room.databaseBuilder(context, CaCountdownDatabase::class.java, CaCountdownDatabase.NAME)
            // No released users yet, so a destructive fallback is the pragmatic choice
            // while the schema is still churning; swap for real Migrations before launch.
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideExamScheduleDao(db: CaCountdownDatabase): ExamScheduleDao = db.examScheduleDao()

    @Provides
    fun provideEffectiveExamDao(db: CaCountdownDatabase): EffectiveExamDao = db.effectiveExamDao()

    @Provides
    fun provideUserSetupDao(db: CaCountdownDatabase): UserSetupDao = db.userSetupDao()

    @Provides
    fun provideExamOverrideDao(db: CaCountdownDatabase): ExamOverrideDao = db.examOverrideDao()

    @Provides
    fun provideSubjectProgressDao(db: CaCountdownDatabase): SubjectProgressDao = db.subjectProgressDao()

    @Provides
    fun provideStudySessionDao(db: CaCountdownDatabase): StudySessionDao = db.studySessionDao()

    @Provides
    fun provideReminderLogDao(db: CaCountdownDatabase): ReminderLogDao = db.reminderLogDao()

    @Provides
    fun providePendingScheduleUpdateDao(db: CaCountdownDatabase): PendingScheduleUpdateDao = db.pendingScheduleUpdateDao()

    @Provides
    fun provideNoteDao(db: CaCountdownDatabase): NoteDao = db.noteDao()

    @Provides
    fun provideChecklistDao(db: CaCountdownDatabase): ChecklistDao = db.checklistDao()

    @Provides
    fun provideTimetableDao(db: CaCountdownDatabase): TimetableDao = db.timetableDao()

    @Provides
    fun provideAlarmDao(db: CaCountdownDatabase): AlarmDao = db.alarmDao()

    @Provides
    fun provideGoalDao(db: CaCountdownDatabase): GoalDao = db.goalDao()

    @Provides
    fun provideChapterDao(db: CaCountdownDatabase): ChapterDao = db.chapterDao()

    @Provides
    fun providePlannedActivityDao(db: CaCountdownDatabase): PlannedActivityDao = db.plannedActivityDao()
}
