package com.cacountdown.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

/** A free-text note, optionally scoped to a subject. */
@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String?,
    val title: String,
    val body: String,
    val createdAt: Instant,
    val updatedAt: Instant,
)

/** A single to-do item, optionally scoped to a subject. */
@Entity(tableName = "checklist_item")
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String?,
    val text: String,
    val isDone: Boolean,
    val createdAt: Instant,
)

/**
 * One block in the student's own weekly study timetable — separate from the
 * official [ExamScheduleEntity] exam dates. `dayOfWeek` is 1 (Monday) to 7
 * (Sunday), ISO-8601 style, matching [java.time.DayOfWeek.getValue].
 */
@Entity(tableName = "timetable_entry")
data class TimetableEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String?,
    val title: String,
    val dayOfWeek: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
)

/**
 * A student-set alarm, scheduled through [android.app.AlarmManager]. Reset
 * after every reboot by `RescheduleAlarmsWorker`, same as the exact exam-day
 * alerts (spec §: Reminders and background work).
 */
@Entity(tableName = "study_alarm")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val subjectCode: String?,
    val hour: Int,
    val minute: Int,
    val repeatDaily: Boolean,
    val isEnabled: Boolean,
)

/**
 * A target or goal the student sets for themself: either scoped to one
 * subject (a target, e.g. "finish 20 chapters by 1 Dec") or attempt-wide
 * (a goal, e.g. "average 6h/day"). `currentValue` is nudged by the student
 * via the Goals screen rather than derived automatically.
 */
@Entity(tableName = "goal")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String?,
    val title: String,
    val unit: String, // GoalUnit name
    val targetValue: Double,
    val currentValue: Double,
    val deadline: LocalDate?,
    val createdAt: Instant,
    val isAchieved: Boolean,
)
