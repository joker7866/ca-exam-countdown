package com.cacountdown.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.repository.ExamRepository
import com.cacountdown.app.domain.model.CaCourse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val name: String = "",
    val age: String = "",
    val course: CaCourse = CaCourse.INTERMEDIATE,
    val attempt: String = "",
    val groups: String = "BOTH",
    val saved: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val examRepository: ExamRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    init {
        viewModelScope.launch {
            examRepository.observeUserSetup().collect { setup ->
                if (setup != null) {
                    _state.value = _state.value.copy(
                        name = setup.name,
                        age = setup.age?.toString() ?: "",
                        course = CaCourse.fromStored(setup.level),
                        attempt = setup.attempt,
                        groups = setup.groups,
                    )
                }
            }
        }
    }

    fun updateName(name: String) { _state.value = _state.value.copy(name = name, saved = false) }
    fun updateAge(age: String) {
        if (age.isEmpty() || age.all(Char::isDigit)) _state.value = _state.value.copy(age = age, saved = false)
    }
    fun selectCourse(course: CaCourse) { _state.value = _state.value.copy(course = course, saved = false) }

    fun save() {
        viewModelScope.launch {
            val s = _state.value
            examRepository.saveUserSetup(
                name = s.name.trim(),
                age = s.age.toIntOrNull(),
                level = s.course.stored,
                attempt = s.attempt,
                groups = s.groups,
            )
            _state.value = _state.value.copy(saved = true)
        }
    }
}
