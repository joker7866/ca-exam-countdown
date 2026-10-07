package com.cacountdown.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.repository.ExamRepository
import com.cacountdown.app.data.repository.ProgressRepository
import com.cacountdown.app.domain.model.DailyTarget
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.model.SubjectProgress
import com.cacountdown.app.domain.model.UrgencyStatus
import com.cacountdown.app.domain.usecase.CalculateDailyTarget
import com.cacountdown.app.domain.usecase.CalculateUrgency
import com.cacountdown.app.domain.usecase.GetCountdowns
import com.cacountdown.app.domain.usecase.GetNextExam
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

// Placeholder until the open question in the plan ("who sets estimated hours per
// subject?") is answered; until then every subject gets the same budget.
private const val DEFAULT_ESTIMATED_HOURS = 80.0
private const val DEFAULT_TOTAL_CHAPTERS = 20

data class SubjectRow(
    val exam: Exam,
    val state: ExamState,
    val progress: SubjectProgress,
    val dailyTarget: DailyTarget,
    val urgency: UrgencyStatus,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val attempt: String = "",
    val rows: List<SubjectRow> = emptyList(),
    val nextExam: SubjectRow? = null,
    val attemptComplete: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val progressRepository: ProgressRepository,
    private val getCountdowns: GetCountdowns,
    private val getNextExam: GetNextExam,
    private val calculateDailyTarget: CalculateDailyTarget,
    private val calculateUrgency: CalculateUrgency,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        viewModelScope.launch {
            val setup = examRepository.observeUserSetup().first() ?: return@launch
            val setupDate = setup.createdAt.atZone(clock.zone).toLocalDate()

            val exams = examRepository.observeExamsForAttempt(setup.attempt).first()
            exams.forEach { exam ->
                progressRepository.ensureExists(exam.subjectCode, DEFAULT_TOTAL_CHAPTERS, DEFAULT_ESTIMATED_HOURS)
            }

            progressRepository.observeAll()
                .map { progressList -> exams to progressList }
                .onEach { (currentExams, progressList) ->
                    buildState(setup.attempt, currentExams, progressList, setupDate)
                }
                .launchIn(viewModelScope)
        }
    }

    private fun buildState(
        attempt: String,
        exams: List<Exam>,
        progressList: List<SubjectProgress>,
        setupDate: LocalDate,
    ) {
        val progressByCode = progressList.associateBy { it.subjectCode }
        val today = LocalDate.now(clock)
        val daysSinceSetup = ChronoUnit.DAYS.between(setupDate, today)

        val rows = exams.mapNotNull { exam ->
            val progress = progressByCode[exam.subjectCode] ?: return@mapNotNull null
            val countdown = getCountdowns(exam)
            val daysRemaining = (countdown.state as? ExamState.DaysLeft)?.days ?: 0L
            val target = calculateDailyTarget(progress, daysRemaining.coerceAtLeast(0))
            val urgency = calculateUrgency(progress, daysRemaining.coerceAtLeast(0), daysSinceSetup, target)
            SubjectRow(exam, countdown.state, progress, target, urgency)
        }

        val next = getNextExam(exams)
        val nextRow = rows.firstOrNull { it.exam.subjectCode == next?.exam?.subjectCode }

        _state.value = HomeUiState(
            isLoading = false,
            attempt = attempt,
            rows = rows.sortedBy { it.exam.examDate },
            nextExam = nextRow,
            attemptComplete = getNextExam.isAttemptComplete(exams),
        )
    }
}
