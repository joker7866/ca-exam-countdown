package com.cacountdown.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

/**
 * One syllabus topic under a subject — the "subject inside topic checklist".
 * [SubjectProgress.syllabusFraction][com.cacountdown.app.domain.model.SubjectProgress.syllabusFraction]
 * is derived by counting these rather than from a separate stored counter,
 * so ticking a topic here is the one and only way syllabus % moves.
 */
@Entity(tableName = "chapter")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String,
    val title: String,
    val orderIndex: Int,
    val isDone: Boolean,
)

enum class ActivityType { STUDY, REVISION, MOCK_TEST }

/**
 * A dated, one-off plan item — "schedule tomorrow's study", "schedule a
 * revision", "schedule a mock test" — distinct from [TimetableEntryEntity],
 * which is the recurring *weekly* slot. Both feed the same Schedule agenda.
 */
@Entity(tableName = "planned_activity")
data class PlannedActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String?,
    val type: ActivityType,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isDone: Boolean,
)
