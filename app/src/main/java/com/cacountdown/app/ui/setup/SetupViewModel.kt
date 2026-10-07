package com.cacountdown.app.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.repository.ExamRepository
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.usecase.GetCountdowns
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Attempt(val id: String, val label: String, val assetFile: String?)

val AVAILABLE_ATTEMPTS = listOf(
    Attempt("2027-01", "Jan 2027", "timetable_inter_2027-01.json"),
    Attempt("2027-05", "May 2027", null),
    Attempt("2027-09", "Sep 2027", null),
)

enum class GroupChoice(val stored: String) { GROUP_1("G1"), GROUP_2("G2"), BOTH("BOTH") }

data class SetupUiState(
    val selectedAttempt: Attempt = AVAILABLE_ATTEMPTS.first(),
    val selectedGroups: GroupChoice = GroupChoice.BOTH,
    val loadedExams: List<Exam> = emptyList(),
    val isLoading: Boolean = false,
    val canStart: Boolean = false,
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val getCountdowns: GetCountdowns,
) : ViewModel() {

    private val _state = MutableStateFlow(SetupUiState())
    val state: StateFlow<SetupUiState> = _state

    init {
        loadExamsForSelectedAttempt()
    }

    fun selectAttempt(attempt: Attempt) {
        _state.value = _state.value.copy(selectedAttempt = attempt)
        loadExamsForSelectedAttempt()
    }

    fun selectGroups(groups: GroupChoice) {
        _state.value = _state.value.copy(selectedGroups = groups)
    }

    private fun loadExamsForSelectedAttempt() {
        val attempt = _state.value.selectedAttempt
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            attempt.assetFile?.let { examRepository.loadBundledTimetableIfEmpty(it, attempt.id) }
            val exams = examRepository.observeExamsForAttempt(attempt.id).first()
            _state.value = _state.value.copy(
                loadedExams = exams,
                isLoading = false,
                canStart = exams.isNotEmpty(),
            )
        }
    }

    fun startCountdown(onComplete: () -> Unit) {
        viewModelScope.launch {
            val s = _state.value
            examRepository.saveUserSetup(
                level = "INTER",
                attempt = s.selectedAttempt.id,
                groups = s.selectedGroups.stored,
            )
            onComplete()
        }
    }

    fun daysAway(exam: Exam): Long {
        val state = getCountdowns(exam).state
        return (state as? com.cacountdown.app.domain.model.ExamState.DaysLeft)?.days ?: 0
    }
}
