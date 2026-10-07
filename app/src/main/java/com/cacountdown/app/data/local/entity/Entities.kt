package com.cacountdown.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.cacountdown.app.domain.model.ExamGroup
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime

/** Official data from server or bundled JSON (spec §12). Never edited in place by the student. */
@Entity(
    tableName = "exam_schedule",
    indices = [Index(value = ["attempt", "subjectCode"], unique = true)],
)
data class ExamScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val attempt: String, // e.g. "2027-01"
    val level: String, // "INTER"
    val subjectCode: String,
    val subjectName: String,
    val group: ExamGroup,
    val examDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val officialNoticeUrl: String?,
    val version: Int,
    val lastUpdated: Instant,
)

/** One row: the student's setup choices. */
@Entity(tableName = "user_setup")
data class UserSetupEntity(
    @PrimaryKey val id: Int = 1,
    val level: String,
    val attempt: String,
    val groups: String, // "G1" | "G2" | "BOTH"
    val createdAt: Instant,
)

/** Student's manual edits; wins over [ExamScheduleEntity] for the same subject. */
@Entity(tableName = "exam_override")
data class ExamOverrideEntity(
    @PrimaryKey val subjectCode: String,
    val customDate: LocalDate?,
    val customStartTime: LocalTime?,
    val editedAt: Instant,
)

/** Drives syllabus %, revision %, hours left. */
@Entity(tableName = "subject_progress")
data class SubjectProgressEntity(
    @PrimaryKey val subjectCode: String,
    val totalChapters: Int,
    val chaptersDone: Int,
    val revisionsPlanned: Int,
    val revisionsDone: Int,
    val mocksPlanned: Int,
    val mocksDone: Int,
    val estimatedHoursTotal: Double,
)

enum class SessionStatus { PLANNED, DONE, MISSED }

/** Drives today's target and missed sessions. */
@Entity(tableName = "study_session")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String,
    val plannedStart: Instant,
    val plannedEnd: Instant,
    val actualMinutes: Int?,
    val status: SessionStatus,
)

/** Prevents sending the same milestone twice. */
@Entity(
    tableName = "reminder_log",
    primaryKeys = ["subjectCode", "milestoneDays"],
)
data class ReminderLogEntity(
    val subjectCode: String,
    val milestoneDays: Int,
    val sentAt: Instant,
)

enum class ScheduleUpdateStatus { NEW, ACCEPTED, KEPT }

/** Holds a new timetable until the student reviews it. */
@Entity(tableName = "pending_schedule_update")
data class PendingScheduleUpdateEntity(
    @PrimaryKey val version: Int,
    val changesJson: String,
    val receivedAt: Instant,
    val status: ScheduleUpdateStatus,
)
