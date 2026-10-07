package com.cacountdown.app.ui.plan

import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class PlanUiState(
    val isLoading: Boolean = true,
    val exam: Exam? = null,
    val state: ExamState? = null,
    val progress: SubjectProgress? = null,
    val dailyTarget: DailyTarget? = null,
    val urgency: UrgencyStatus? = null,
    val expectedSyllabusPercent: Int = 0,
)

@HiltViewModel
class PlanViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val examRepository: ExamRepository,
    private val progressRepository: ProgressRepository,
    private val getCountdowns: GetCountdowns,
    private val getNextExam: GetNextExam,
    private val calculateDailyTarget: CalculateDailyTarget,
    private val calculateUrgency: CalculateUrgency,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(PlanUiState())
    val state: StateFlow<PlanUiState> = _state

    init {
        val subjectCodeArg: String = savedStateHandle["subjectCode"] ?: "next"
        viewModelScope.launch {
            val setup = examRepository.observeUserSetup().first() ?: return@launch
            val setupDate = setup.createdAt.atZone(clock.zone).toLocalDate()
            val exams = examRepository.observeExamsForAttempt(setup.attempt).first()

            val exam = if (subjectCodeArg == "next") {
                getNextExam(exams)?.exam
            } else {
                exams.firstOrNull { it.subjectCode == subjectCodeArg }
            } ?: return@launch

            progressRepository.observe(exam.subjectCode)
                .onEach { progress -> buildState(exam, progress, setupDate) }
                .launchIn(viewModelScope)
        }
    }

    private fun buildState(exam: Exam, progress: SubjectProgress?, setupDate: LocalDate) {
        if (progress == null) {
            _state.value = _state.value.copy(isLoading = true, exam = exam)
            return
        }
        val countdown = getCountdowns(exam)
        val daysRemaining = (countdown.state as? ExamState.DaysLeft)?.days ?: 0L
        val daysSinceSetup = ChronoUnit.DAYS.between(setupDate, LocalDate.now(clock))
        val target = calculateDailyTarget(progress, daysRemaining.coerceAtLeast(0))
        val urgency = calculateUrgency(progress, daysRemaining.coerceAtLeast(0), daysSinceSetup, target)
        val expected = calculateUrgency.expectedSyllabusFraction(daysSinceSetup, daysRemaining.coerceAtLeast(0))

        _state.value = PlanUiState(
            isLoading = false,
            exam = exam,
            state = countdown.state,
            progress = progress,
            dailyTarget = target,
            urgency = urgency,
            expectedSyllabusPercent = (expected * 100).toInt(),
        )
    }

    fun markChapterDone() {
        viewModelScope.launch {
            val code = _state.value.exam?.subjectCode ?: return@launch
            progressRepository.markChapterDone(code)
        }
    }
}
