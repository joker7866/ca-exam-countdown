package com.cacountdown.app.data.repository

import com.cacountdown.app.data.local.dao.SubjectProgressDao
import com.cacountdown.app.data.local.entity.SubjectProgressEntity
import com.cacountdown.app.domain.model.SubjectProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val subjectProgressDao: SubjectProgressDao,
) {
    fun observe(subjectCode: String): Flow<SubjectProgress?> =
        subjectProgressDao.observe(subjectCode).map { it?.toDomain() }

    fun observeAll(): Flow<List<SubjectProgress>> =
        subjectProgressDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    /** Inserts a zeroed progress row the first time a subject is seen; never overwrites existing progress. */
    suspend fun ensureExists(subjectCode: String, totalChapters: Int, estimatedHoursTotal: Double) {
        if (subjectProgressDao.exists(subjectCode)) return
        subjectProgressDao.upsert(
            SubjectProgressEntity(
                subjectCode = subjectCode,
                totalChapters = totalChapters,
                chaptersDone = 0,
                revisionsPlanned = 3,
                revisionsDone = 0,
                mocksPlanned = 4,
                mocksDone = 0,
                estimatedHoursTotal = estimatedHoursTotal,
            ),
        )
    }

    suspend fun markChapterDone(subjectCode: String) {
        val current = subjectProgressDao.getOnce(subjectCode) ?: return
        subjectProgressDao.update(current.copy(chaptersDone = (current.chaptersDone + 1).coerceAtMost(current.totalChapters)))
    }

    private fun SubjectProgressEntity.toDomain() = SubjectProgress(
        subjectCode = subjectCode,
        totalChapters = totalChapters,
        chaptersDone = chaptersDone,
        revisionsPlanned = revisionsPlanned,
        revisionsDone = revisionsDone,
        mocksPlanned = mocksPlanned,
        mocksDone = mocksDone,
        estimatedHoursTotal = estimatedHoursTotal,
    )
}
