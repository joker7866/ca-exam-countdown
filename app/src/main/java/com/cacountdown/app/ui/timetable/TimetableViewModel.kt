package com.cacountdown.app.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.TimetableDao
import com.cacountdown.app.data.local.entity.TimetableEntryEntity
import com.cacountdown.app.data.repository.SubjectOptionsSource
import com.cacountdown.app.domain.model.Exam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val timetableDao: TimetableDao,
    private val subjectOptionsSource: SubjectOptionsSource,
) : ViewModel() {

    val entries: StateFlow<List<TimetableEntryEntity>> = timetableDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subjects = MutableStateFlow<List<Exam>>(emptyList())
    val subjects: StateFlow<List<Exam>> = _subjects

    init {
        viewModelScope.launch { _subjects.value = subjectOptionsSource.current() }
    }

    fun addEntry(title: String, dayOfWeek: Int, startTime: LocalTime, endTime: LocalTime, subjectCode: String?) {
        if (title.isBlank()) return
        viewModelScope.launch {
            timetableDao.insert(
                TimetableEntryEntity(
                    subjectCode = subjectCode,
                    title = title.trim(),
                    dayOfWeek = dayOfWeek,
                    startTime = startTime,
                    endTime = endTime,
                ),
            )
        }
    }

    fun deleteEntry(entry: TimetableEntryEntity) {
        viewModelScope.launch { timetableDao.delete(entry) }
    }

    fun subjectName(code: String?): String? = code?.let { c -> _subjects.value.firstOrNull { it.subjectCode == c }?.subjectName }
}
