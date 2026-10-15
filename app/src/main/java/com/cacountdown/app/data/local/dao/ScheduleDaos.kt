package com.cacountdown.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cacountdown.app.data.local.entity.ChapterEntity
import com.cacountdown.app.data.local.entity.PlannedActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ChapterDao {
    @Insert
    suspend fun insert(chapter: ChapterEntity): Long

    @Insert
    suspend fun insertAll(chapters: List<ChapterEntity>)

    @Update
    suspend fun update(chapter: ChapterEntity)

    @Delete
    suspend fun delete(chapter: ChapterEntity)

    @Query("SELECT * FROM chapter WHERE subjectCode = :subjectCode ORDER BY orderIndex")
    fun observeForSubject(subjectCode: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapter ORDER BY subjectCode, orderIndex")
    fun observeAll(): Flow<List<ChapterEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM chapter WHERE subjectCode = :subjectCode)")
    suspend fun existsForSubject(subjectCode: String): Boolean
}

@Dao
interface PlannedActivityDao {
    @Insert
    suspend fun insert(activity: PlannedActivityEntity): Long

    @Update
    suspend fun update(activity: PlannedActivityEntity)

    @Delete
    suspend fun delete(activity: PlannedActivityEntity)

    @Query("SELECT * FROM planned_activity ORDER BY date, startTime")
    fun observeAll(): Flow<List<PlannedActivityEntity>>

    @Query("SELECT * FROM planned_activity WHERE subjectCode = :subjectCode ORDER BY date, startTime")
    fun observeForSubject(subjectCode: String): Flow<List<PlannedActivityEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM planned_activity WHERE subjectCode = :subjectCode AND date < :today AND isDone = 0)")
    suspend fun hasOverdueForSubject(subjectCode: String, today: LocalDate): Boolean
}
