package com.cacountdown.app.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.PlannedActivityDao
import com.cacountdown.app.data.repository.ExamRepository
import com.cacountdown.app.data.repository.ProgressRepository
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.model.SubjectProgress
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.domain.usecase.CalculateDailyTarget
import com.cacountdown.app.domain.usecase.CalculateUrgency
import com.cacountdown.app.domain.usecase.GetCountdowns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class SubjectAnalysis(
    val exam: Exam,
    val progress: SubjectProgress,
    val urgency: UrgencyStatus,
)

data class AnalysisUiState(
    val isLoading: Boolean = true,
    val rows: List<SubjectAnalysis> = emptyList(),
) {
    val urgencyCounts: Map<UrgencyStatus, Int>
        get() = UrgencyStatus.entries.associateWith { status -> rows.count { it.urgency == status } }
}

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val progressRepository: ProgressRepository,
    private val plannedActivityDao: PlannedActivityDao,
    private val getCountdowns: GetCountdowns,
    private val calculateDailyTarget: CalculateDailyTarget,
    private val calculateUrgency: CalculateUrgency,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisUiState())
    val state: StateFlow<AnalysisUiState> = _state

    init {
        viewModelScope.launch {
            val setup = examRepository.observeUserSetup().first() ?: return@launch
            val setupDate = setup.createdAt.atZone(clock.zone).toLocalDate()
            val exams = examRepository.observeExamsForAttempt(setup.attempt).first()

            progressRepository.observeAll()
                .map { progressList -> exams to progressList }
                .onEach { (currentExams, progressList) -> buildState(currentExams, progressList, setupDate) }
                .launchIn(viewModelScope)
        }
    }

    private suspend fun buildState(exams: List<Exam>, progressList: List<SubjectProgress>, setupDate: LocalDate) {
        val progressByCode = progressList.associateBy { it.subjectCode }
        val today = LocalDate.now(clock)
        val daysSinceSetup = ChronoUnit.DAYS.between(setupDate, today)

        val rows = exams.mapNotNull { exam ->
            val progress = progressByCode[exam.subjectCode] ?: return@mapNotNull null
            val countdown = getCountdowns(exam)
            val daysRemaining = (countdown.state as? ExamState.DaysLeft)?.days ?: 0L
            val target = calculateDailyTarget(progress, daysRemaining.coerceAtLeast(0))
            val daysSinceLastActivity = progress.lastActivityAt
                ?.let { ChronoUnit.DAYS.between(it.atZone(clock.zone).toLocalDate(), today) }
            val hasOverdue = plannedActivityDao.hasOverdueForSubject(exam.subjectCode, today)
            val urgency = calculateUrgency(
                progress,
                daysRemaining.coerceAtLeast(0),
                daysSinceSetup,
                target,
                daysSinceLastActivity,
                hasOverdue,
            )
            SubjectAnalysis(exam, progress, urgency)
        }

        _state.value = AnalysisUiState(isLoading = false, rows = rows.sortedBy { it.exam.examDate })
    }
}
