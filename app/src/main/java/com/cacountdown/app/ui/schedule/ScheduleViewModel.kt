package com.cacountdown.app.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.PlannedActivityDao
import com.cacountdown.app.data.local.entity.ActivityType
import com.cacountdown.app.data.local.entity.PlannedActivityEntity
import com.cacountdown.app.data.repository.ProgressRepository
import com.cacountdown.app.data.repository.SubjectOptionsSource
import com.cacountdown.app.domain.model.Exam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val plannedActivityDao: PlannedActivityDao,
    private val subjectOptionsSource: SubjectOptionsSource,
    private val progressRepository: ProgressRepository,
    private val clock: Clock,
) : ViewModel() {

    val activities: StateFlow<List<PlannedActivityEntity>> = plannedActivityDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subjects = MutableStateFlow<List<Exam>>(emptyList())
    val subjects: StateFlow<List<Exam>> = _subjects

    init {
        viewModelScope.launch { _subjects.value = subjectOptionsSource.current() }
    }

    fun today(): LocalDate = LocalDate.now(clock)

    fun addActivity(
        title: String,
        type: ActivityType,
        subjectCode: String?,
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            plannedActivityDao.insert(
                PlannedActivityEntity(
                    subjectCode = subjectCode,
                    type = type,
                    title = title.trim(),
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    isDone = false,
                ),
            )
        }
    }

    fun toggleDone(activity: PlannedActivityEntity) {
        viewModelScope.launch {
            val updated = activity.copy(isDone = !activity.isDone)
            plannedActivityDao.update(updated)
            if (updated.isDone && updated.subjectCode != null) {
                progressRepository.markSubjectActivity(updated.subjectCode)
            }
        }
    }

    fun deleteActivity(activity: PlannedActivityEntity) {
        viewModelScope.launch { plannedActivityDao.delete(activity) }
    }

    fun subjectName(code: String?): String? = code?.let { c -> _subjects.value.firstOrNull { it.subjectCode == c }?.subjectName }
}
