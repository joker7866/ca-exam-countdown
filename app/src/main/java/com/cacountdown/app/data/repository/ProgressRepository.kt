package com.cacountdown.app.data.repository

import com.cacountdown.app.data.local.dao.ChapterDao
import com.cacountdown.app.data.local.dao.SubjectProgressDao
import com.cacountdown.app.data.local.entity.ChapterEntity
import com.cacountdown.app.data.local.entity.SubjectProgressEntity
import com.cacountdown.app.domain.model.SubjectProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_CHAPTER_COUNT = 10

@Singleton
class ProgressRepository @Inject constructor(
    private val subjectProgressDao: SubjectProgressDao,
    private val chapterDao: ChapterDao,
) {
    fun observe(subjectCode: String): Flow<SubjectProgress?> =
        combine(subjectProgressDao.observe(subjectCode), chapterDao.observeForSubject(subjectCode)) { progress, chapters ->
            progress?.toDomain(chapters)
        }

    fun observeAll(): Flow<List<SubjectProgress>> =
        combine(subjectProgressDao.observeAll(), chapterDao.observeAll()) { progressRows, allChapters ->
            val chaptersBySubject = allChapters.groupBy { it.subjectCode }
            progressRows.map { it.toDomain(chaptersBySubject[it.subjectCode].orEmpty()) }
        }

    fun observeChapters(subjectCode: String): Flow<List<ChapterEntity>> = chapterDao.observeForSubject(subjectCode)

    suspend fun addChapter(subjectCode: String, title: String) {
        if (title.isBlank()) return
        val nextOrderIndex = (chapterDao.observeForSubject(subjectCode).first().maxOfOrNull { it.orderIndex } ?: 0) + 1
        chapterDao.insert(ChapterEntity(subjectCode = subjectCode, title = title.trim(), orderIndex = nextOrderIndex, isDone = false))
    }

    /** Toggles a topic's done state, and — when it's newly marked done — counts that as activity on the subject (spec: Attention also fires when nothing's been studied in a few days). */
    suspend fun toggleChapter(chapter: ChapterEntity) {
        val updated = chapter.copy(isDone = !chapter.isDone)
        chapterDao.update(updated)
        if (updated.isDone) {
            markSubjectActivity(chapter.subjectCode)
        }
    }

    suspend fun deleteChapter(chapter: ChapterEntity) {
        chapterDao.delete(chapter)
    }

    /** Records that the student just did something for this subject — a completed topic or a completed scheduled session. */
    suspend fun markSubjectActivity(subjectCode: String) {
        val current = subjectProgressDao.getOnce(subjectCode) ?: return
        subjectProgressDao.update(current.copy(lastActivityAt = Instant.now()))
    }

    /** Inserts a zeroed progress row and the official chapter list the first time a subject is seen; never overwrites existing data. */
    suspend fun ensureExists(subjectCode: String, estimatedHoursTotal: Double) {
        if (!subjectProgressDao.exists(subjectCode)) {
            subjectProgressDao.upsert(
                SubjectProgressEntity(
                    subjectCode = subjectCode,
                    revisionsPlanned = 3,
                    revisionsDone = 0,
                    mocksPlanned = 4,
                    mocksDone = 0,
                    estimatedHoursTotal = estimatedHoursTotal,
                ),
            )
        }
        if (!chapterDao.existsForSubject(subjectCode)) {
            val titles = OfficialSyllabus.chaptersFor(subjectCode)
                ?: (1..DEFAULT_CHAPTER_COUNT).map { n -> "Chapter $n" }
            chapterDao.insertAll(
                titles.mapIndexed { index, title ->
                    ChapterEntity(subjectCode = subjectCode, title = title, orderIndex = index + 1, isDone = false)
                },
            )
        }
    }

    private fun SubjectProgressEntity.toDomain(chapters: List<ChapterEntity>) = SubjectProgress(
        subjectCode = subjectCode,
        totalChapters = chapters.size,
        chaptersDone = chapters.count { it.isDone },
        revisionsPlanned = revisionsPlanned,
        revisionsDone = revisionsDone,
        mocksPlanned = mocksPlanned,
        mocksDone = mocksDone,
        estimatedHoursTotal = estimatedHoursTotal,
        lastActivityAt = lastActivityAt,
    )
}
