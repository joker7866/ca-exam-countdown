package com.cacountdown.app.data.repository

import android.content.Context
import com.cacountdown.app.data.local.dao.EffectiveExamDao
import com.cacountdown.app.data.local.dao.ExamOverrideDao
import com.cacountdown.app.data.local.dao.ExamScheduleDao
import com.cacountdown.app.data.local.dao.UserSetupDao
import com.cacountdown.app.data.local.entity.ExamOverrideEntity
import com.cacountdown.app.data.local.entity.ExamScheduleEntity
import com.cacountdown.app.data.local.entity.UserSetupEntity
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamGroup
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BundledTimetable(
    val version: Int,
    val level: String,
    val attempt: String,
    val publishedAt: String,
    val officialNoticeUrl: String? = null,
    val exams: List<BundledExam>,
)

@Serializable
data class BundledExam(
    val subjectCode: String,
    val subjectName: String,
    val group: String, // "G1" | "G2"
    val examDate: String, // ISO yyyy-MM-dd
    val startTime: String, // HH:mm
    val endTime: String, // HH:mm
)

/**
 * The single source of truth for exam dates. Reads always go through
 * `effective_exam` (override-wins join); writes to the official table only
 * ever come from [loadBundledTimetableIfEmpty] or a future schedule sync —
 * never from the UI directly, so a student's manual edit always lands in
 * `exam_override` instead.
 */
@Singleton
class ExamRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val examScheduleDao: ExamScheduleDao,
    private val effectiveExamDao: EffectiveExamDao,
    private val examOverrideDao: ExamOverrideDao,
    private val userSetupDao: UserSetupDao,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun observeUserSetup(): Flow<UserSetupEntity?> = userSetupDao.observe()

    suspend fun saveUserSetup(name: String, age: Int?, level: String, attempt: String, groups: String) {
        userSetupDao.upsert(
            UserSetupEntity(
                name = name,
                age = age,
                level = level,
                attempt = attempt,
                groups = groups,
                createdAt = Instant.now(),
            ),
        )
    }

    fun observeExamsForAttempt(attempt: String): Flow<List<Exam>> =
        effectiveExamDao.observeForAttempt(attempt).map { rows ->
            rows.map { Exam(it.subjectCode, it.subjectName, it.group, it.examDate, it.startTime, it.endTime) }
        }

    suspend fun setOverride(subjectCode: String, date: LocalDate, startTime: LocalTime) {
        examOverrideDao.upsert(
            ExamOverrideEntity(
                subjectCode = subjectCode,
                customDate = date,
                customStartTime = startTime,
                editedAt = Instant.now(),
            ),
        )
    }

    /** Setup works offline on first launch: loads the bundled JSON only if nothing is stored yet for this attempt. */
    suspend fun loadBundledTimetableIfEmpty(assetFileName: String, attempt: String) {
        if (examScheduleDao.latestVersion(attempt) != null) return

        val raw = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        val bundled = json.decodeFromString(BundledTimetable.serializer(), raw)

        val entities = bundled.exams.map { e ->
            ExamScheduleEntity(
                attempt = bundled.attempt,
                level = bundled.level,
                subjectCode = e.subjectCode,
                subjectName = e.subjectName,
                group = if (e.group == "G1") ExamGroup.GROUP_1 else ExamGroup.GROUP_2,
                examDate = LocalDate.parse(e.examDate),
                startTime = LocalTime.parse(e.startTime),
                endTime = LocalTime.parse(e.endTime),
                officialNoticeUrl = bundled.officialNoticeUrl,
                version = bundled.version,
                lastUpdated = Instant.now(),
            )
        }
        examScheduleDao.insertAll(entities)
    }
}
