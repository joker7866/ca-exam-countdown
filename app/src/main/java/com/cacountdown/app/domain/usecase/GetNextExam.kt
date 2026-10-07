package com.cacountdown.app.domain.usecase

import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamCountdown
import com.cacountdown.app.domain.model.ExamState
import javax.inject.Inject

/**
 * Next exam = the first exam (sorted by date, then start time) whose state
 * is not Completed. When every exam is done, there is no next exam — Home
 * shows the "Attempt complete" state for that case.
 */
class GetNextExam @Inject constructor(private val getCountdowns: GetCountdowns) {

    operator fun invoke(exams: List<Exam>): ExamCountdown? {
        return exams
            .sortedWith(compareBy({ it.examDate }, { it.startTime }))
            .map { getCountdowns(it) }
            .firstOrNull { it.state !is ExamState.Completed }
    }

    /** All exams that have already passed, most recent first — used for the post-attempt summary. */
    fun completed(exams: List<Exam>): List<ExamCountdown> {
        return exams
            .map { getCountdowns(it) }
            .filter { it.state is ExamState.Completed }
            .sortedByDescending { it.exam.examDate }
    }

    fun isAttemptComplete(exams: List<Exam>): Boolean = invoke(exams) == null && exams.isNotEmpty()
}
