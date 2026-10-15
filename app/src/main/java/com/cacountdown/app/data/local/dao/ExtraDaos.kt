package com.cacountdown.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cacountdown.app.data.local.entity.AlarmEntity
import com.cacountdown.app.data.local.entity.ChecklistItemEntity
import com.cacountdown.app.data.local.entity.GoalEntity
import com.cacountdown.app.data.local.entity.NoteEntity
import com.cacountdown.app.data.local.entity.TimetableEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM note ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM note WHERE subjectCode = :subjectCode ORDER BY updatedAt DESC")
    fun observeForSubject(subjectCode: String): Flow<List<NoteEntity>>
}

@Dao
interface ChecklistDao {
    @Insert
    suspend fun insert(item: ChecklistItemEntity): Long

    @Update
    suspend fun update(item: ChecklistItemEntity)

    @Delete
    suspend fun delete(item: ChecklistItemEntity)

    @Query("SELECT * FROM checklist_item ORDER BY isDone, createdAt DESC")
    fun observeAll(): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_item WHERE subjectCode = :subjectCode ORDER BY isDone, createdAt DESC")
    fun observeForSubject(subjectCode: String): Flow<List<ChecklistItemEntity>>

    @Query("DELETE FROM checklist_item WHERE isDone = 1")
    suspend fun deleteCompleted()
}

@Dao
interface TimetableDao {
    @Insert
    suspend fun insert(entry: TimetableEntryEntity): Long

    @Update
    suspend fun update(entry: TimetableEntryEntity)

    @Delete
    suspend fun delete(entry: TimetableEntryEntity)

    @Query("SELECT * FROM timetable_entry ORDER BY dayOfWeek, startTime")
    fun observeAll(): Flow<List<TimetableEntryEntity>>
}

@Dao
interface AlarmDao {
    @Insert
    suspend fun insert(alarm: AlarmEntity): Long

    @Update
    suspend fun update(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("SELECT * FROM study_alarm ORDER BY hour, minute")
    fun observeAll(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM study_alarm WHERE isEnabled = 1")
    suspend fun allEnabled(): List<AlarmEntity>
}

@Dao
interface GoalDao {
    @Insert
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Delete
    suspend fun delete(goal: GoalEntity)

    @Query("SELECT * FROM goal ORDER BY isAchieved, deadline IS NULL, deadline")
    fun observeAll(): Flow<List<GoalEntity>>
}
