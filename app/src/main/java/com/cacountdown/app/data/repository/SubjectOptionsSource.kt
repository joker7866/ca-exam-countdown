package com.cacountdown.app.data.repository

import com.cacountdown.app.domain.model.Exam
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** The current attempt's subject list, for the "optional subject" picker on Notes/Checklist/Timetable/Alarms/Goals/Schedule. */
@Singleton
class SubjectOptionsSource @Inject constructor(
    private val examRepository: ExamRepository,
) {
    suspend fun current(): List<Exam> {
        val setup = examRepository.observeUserSetup().first() ?: return emptyList()
        return examRepository.observeExamsForAttempt(setup.attempt).first()
    }
}
