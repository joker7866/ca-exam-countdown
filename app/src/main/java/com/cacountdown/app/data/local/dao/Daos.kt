package com.cacountdown.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cacountdown.app.data.local.entity.EffectiveExamView
import com.cacountdown.app.data.local.entity.ExamOverrideEntity
import com.cacountdown.app.data.local.entity.ExamScheduleEntity
import com.cacountdown.app.data.local.entity.PendingScheduleUpdateEntity
import com.cacountdown.app.data.local.entity.ReminderLogEntity
import com.cacountdown.app.data.local.entity.StudySessionEntity
import com.cacountdown.app.data.local.entity.SubjectProgressEntity
import com.cacountdown.app.data.local.entity.UserSetupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exams: List<ExamScheduleEntity>)

    @Query("SELECT * FROM exam_schedule WHERE attempt = :attempt ORDER BY examDate, startTime")
    fun observeForAttempt(attempt: String): Flow<List<ExamScheduleEntity>>

    @Query("SELECT MAX(version) FROM exam_schedule WHERE attempt = :attempt")
    suspend fun latestVersion(attempt: String): Int?

    @Query("DELETE FROM exam_schedule WHERE attempt = :attempt")
    suspend fun deleteForAttempt(attempt: String)
}

@Dao
interface EffectiveExamDao {
    @Query("SELECT * FROM effective_exam WHERE attempt = :attempt ORDER BY examDate, startTime")
    fun observeForAttempt(attempt: String): Flow<List<EffectiveExamView>>

    @Query("SELECT * FROM effective_exam WHERE subjectCode = :subjectCode LIMIT 1")
    suspend fun bySubject(subjectCode: String): EffectiveExamView?
}

@Dao
interface UserSetupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setup: UserSetupEntity)

    @Query("SELECT * FROM user_setup WHERE id = 1")
    fun observe(): Flow<UserSetupEntity?>
}

@Dao
interface ExamOverrideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(override: ExamOverrideEntity)

    @Delete
    suspend fun delete(override: ExamOverrideEntity)

    @Query("SELECT * FROM exam_override")
    fun observeAll(): Flow<List<ExamOverrideEntity>>

    @Query("SELECT * FROM exam_override WHERE subjectCode = :subjectCode LIMIT 1")
    suspend fun bySubject(subjectCode: String): ExamOverrideEntity?
}

@Dao
interface SubjectProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: SubjectProgressEntity)

    @Update
    suspend fun update(progress: SubjectProgressEntity)

    @Query("SELECT * FROM subject_progress WHERE subjectCode = :subjectCode LIMIT 1")
    fun observe(subjectCode: String): Flow<SubjectProgressEntity?>

    @Query("SELECT * FROM subject_progress WHERE subjectCode = :subjectCode LIMIT 1")
    suspend fun getOnce(subjectCode: String): SubjectProgressEntity?

    @Query("SELECT * FROM subject_progress")
    fun observeAll(): Flow<List<SubjectProgressEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM subject_progress WHERE subjectCode = :subjectCode)")
    suspend fun exists(subjectCode: String): Boolean
}

@Dao
interface StudySessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: StudySessionEntity): Long

    @Update
    suspend fun update(session: StudySessionEntity)

    @Query("SELECT * FROM study_session WHERE subjectCode = :subjectCode ORDER BY plannedStart DESC")
    fun observeForSubject(subjectCode: String): Flow<List<StudySessionEntity>>

    @Query(
        """
        SELECT COUNT(*) FROM study_session
        WHERE subjectCode = :subjectCode AND status = 'MISSED' AND plannedStart >= :sinceEpochMilli
        """,
    )
    suspend fun missedSessionCountSince(subjectCode: String, sinceEpochMilli: Long): Int
}

@Dao
interface ReminderLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: ReminderLogEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM reminder_log WHERE subjectCode = :subjectCode AND milestoneDays = :milestoneDays)")
    suspend fun alreadySent(subjectCode: String, milestoneDays: Int): Boolean
}

@Dao
interface PendingScheduleUpdateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(update: PendingScheduleUpdateEntity)

    @Query("SELECT * FROM pending_schedule_update WHERE status = 'NEW' ORDER BY version DESC LIMIT 1")
    fun observeLatestPending(): Flow<PendingScheduleUpdateEntity?>

    @Update
    suspend fun update(update: PendingScheduleUpdateEntity)
}
