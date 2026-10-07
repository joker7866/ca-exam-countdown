package com.cacountdown.app.data.local.entity

import androidx.room.DatabaseView
import com.cacountdown.app.domain.model.ExamGroup
import java.time.LocalDate
import java.time.LocalTime

/**
 * The effective exam date = override if present, else official. Every
 * screen and background worker reads through this one view so they can
 * never disagree about which date is in force for a subject.
 */
@DatabaseView(
    viewName = "effective_exam",
    value = """
        SELECT
            es.subjectCode AS subjectCode,
            es.subjectName AS subjectName,
            es.attempt AS attempt,
            es.`group` AS `group`,
            COALESCE(eo.customDate, es.examDate) AS examDate,
            COALESCE(eo.customStartTime, es.startTime) AS startTime,
            es.endTime AS endTime,
            CASE WHEN eo.subjectCode IS NOT NULL THEN 1 ELSE 0 END AS isOverridden
        FROM exam_schedule AS es
        LEFT JOIN exam_override AS eo ON eo.subjectCode = es.subjectCode
    """,
)
data class EffectiveExamView(
    val subjectCode: String,
    val subjectName: String,
    val attempt: String,
    val group: ExamGroup,
    val examDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val isOverridden: Boolean,
)
