package com.cacountdown.app.ui.alarms

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.AlarmDao
import com.cacountdown.app.data.local.entity.AlarmEntity
import com.cacountdown.app.data.repository.SubjectOptionsSource
import com.cacountdown.app.domain.model.Exam
import com.cacountdown.app.work.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel @Inject constructor(
    private val application: Application,
    private val alarmDao: AlarmDao,
    private val subjectOptionsSource: SubjectOptionsSource,
) : ViewModel() {

    val alarms: StateFlow<List<AlarmEntity>> = alarmDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subjects = MutableStateFlow<List<Exam>>(emptyList())
    val subjects: StateFlow<List<Exam>> = _subjects

    init {
        viewModelScope.launch { _subjects.value = subjectOptionsSource.current() }
    }

    fun addAlarm(label: String, time: LocalTime, repeatDaily: Boolean, subjectCode: String?) {
        viewModelScope.launch {
            val id = alarmDao.insert(
                AlarmEntity(
                    label = label.ifBlank { "Study alarm" },
                    subjectCode = subjectCode,
                    hour = time.hour,
                    minute = time.minute,
                    repeatDaily = repeatDaily,
                    isEnabled = true,
                ),
            )
            val saved = AlarmEntity(
                id = id,
                label = label.ifBlank { "Study alarm" },
                subjectCode = subjectCode,
                hour = time.hour,
                minute = time.minute,
                repeatDaily = repeatDaily,
                isEnabled = true,
            )
            AlarmScheduler.schedule(application, saved)
        }
    }

    fun toggleEnabled(alarm: AlarmEntity) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            alarmDao.update(updated)
            if (updated.isEnabled) {
                AlarmScheduler.schedule(application, updated)
            } else {
                AlarmScheduler.cancel(application, updated.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancel(application, alarm.id)
            alarmDao.delete(alarm)
        }
    }

    fun subjectName(code: String?): String? = code?.let { c -> _subjects.value.firstOrNull { it.subjectCode == c }?.subjectName }
}
