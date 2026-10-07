package com.cacountdown.app.ui.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.repository.ExamRepository
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.ExamState
import com.cacountdown.app.domain.model.ReminderFrequencyStage
import com.cacountdown.app.domain.model.STANDARD_MILESTONES
import com.cacountdown.app.domain.usecase.GetCountdowns
import com.cacountdown.app.domain.usecase.GetNextExam
import com.cacountdown.app.domain.usecase.PlanReminders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertsUiState(
    val isLoading: Boolean = true,
    val nextExam: Exam? = null,
    val daysRemaining: Long = 0,
    val frequencyStage: ReminderFrequencyStage? = null,
    val milestoneDays: List<Int> = STANDARD_MILESTONES.map { it.daysBefore },
    val allRemindersOn: Boolean = true,
    val milestoneAlertsOn: Boolean = true,
    val progressCheckInsOn: Boolean = true,
    val dailyNudgeOn: Boolean = false,
    val quietHoursOn: Boolean = true,
)

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val getCountdowns: GetCountdowns,
    private val getNextExam: GetNextExam,
    private val planReminders: PlanReminders,
) : ViewModel() {

    private val _state = MutableStateFlow(AlertsUiState())
    val state: StateFlow<AlertsUiState> = _state

    init {
        viewModelScope.launch {
            val setup = examRepository.observeUserSetup().first() ?: return@launch
            val exams = examRepository.observeExamsForAttempt(setup.attempt).first()
            val next = getNextExam(exams)?.exam
            val days = next?.let { (getCountdowns(it).state as? ExamState.DaysLeft)?.days } ?: 0L

            _state.value = _state.value.copy(
                isLoading = false,
                nextExam = next,
                daysRemaining = days,
                frequencyStage = planReminders.frequencyStage(days),
            )
        }
    }

    fun toggleAllReminders(on: Boolean) { _state.value = _state.value.copy(allRemindersOn = on) }
    fun toggleMilestoneAlerts(on: Boolean) { _state.value = _state.value.copy(milestoneAlertsOn = on) }
    fun toggleProgressCheckIns(on: Boolean) { _state.value = _state.value.copy(progressCheckInsOn = on) }
    fun toggleDailyNudge(on: Boolean) { _state.value = _state.value.copy(dailyNudgeOn = on) }
    fun toggleQuietHours(on: Boolean) { _state.value = _state.value.copy(quietHoursOn = on) }
}
