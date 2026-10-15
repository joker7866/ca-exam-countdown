package com.cacountdown.app.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.GoalDao
import com.cacountdown.app.data.local.entity.GoalEntity
import com.cacountdown.app.data.repository.SubjectOptionsSource
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.domain.model.GoalUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val subjectOptionsSource: SubjectOptionsSource,
) : ViewModel() {

    val goals: StateFlow<List<GoalEntity>> = goalDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subjects = MutableStateFlow<List<Exam>>(emptyList())
    val subjects: StateFlow<List<Exam>> = _subjects

    init {
        viewModelScope.launch { _subjects.value = subjectOptionsSource.current() }
    }

    fun subjectName(code: String?): String? = code?.let { c -> _subjects.value.firstOrNull { it.subjectCode == c }?.subjectName }

    fun addGoal(title: String, unit: GoalUnit, targetValue: Double, deadline: LocalDate?, subjectCode: String? = null) {
        if (title.isBlank() || targetValue <= 0) return
        viewModelScope.launch {
            goalDao.insert(
                GoalEntity(
                    subjectCode = subjectCode,
                    title = title.trim(),
                    unit = unit.name,
                    targetValue = targetValue,
                    currentValue = 0.0,
                    deadline = deadline,
                    createdAt = Instant.now(),
                    isAchieved = false,
                ),
            )
        }
    }

    fun addProgress(goal: GoalEntity, amount: Double) {
        viewModelScope.launch {
            val newValue = (goal.currentValue + amount).coerceAtLeast(0.0)
            goalDao.update(goal.copy(currentValue = newValue, isAchieved = newValue >= goal.targetValue))
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch { goalDao.delete(goal) }
    }
}
