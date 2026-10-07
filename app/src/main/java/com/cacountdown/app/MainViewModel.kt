package com.cacountdown.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val examRepository: ExamRepository,
) : ViewModel() {

    private val _hasCompletedSetup = MutableStateFlow<Boolean?>(null)
    val hasCompletedSetup: StateFlow<Boolean?> = _hasCompletedSetup

    init {
        viewModelScope.launch {
            val setup = examRepository.observeUserSetup().first()
            _hasCompletedSetup.value = setup != null
        }
    }
}
