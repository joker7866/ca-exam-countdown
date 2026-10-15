package com.cacountdown.app.ui.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.ChecklistDao
import com.cacountdown.app.data.local.entity.ChecklistItemEntity
import com.cacountdown.app.data.repository.SubjectOptionsSource
import com.cacountdown.app.domain.model.Exam
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val checklistDao: ChecklistDao,
    private val subjectOptionsSource: SubjectOptionsSource,
) : ViewModel() {

    val items: StateFlow<List<ChecklistItemEntity>> = checklistDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subjects = MutableStateFlow<List<Exam>>(emptyList())
    val subjects: StateFlow<List<Exam>> = _subjects

    init {
        viewModelScope.launch { _subjects.value = subjectOptionsSource.current() }
    }

    fun addItem(text: String, subjectCode: String?) {
        if (text.isBlank()) return
        viewModelScope.launch {
            checklistDao.insert(
                ChecklistItemEntity(subjectCode = subjectCode, text = text.trim(), isDone = false, createdAt = Instant.now()),
            )
        }
    }

    fun toggleDone(item: ChecklistItemEntity) {
        viewModelScope.launch { checklistDao.update(item.copy(isDone = !item.isDone)) }
    }

    fun deleteItem(item: ChecklistItemEntity) {
        viewModelScope.launch { checklistDao.delete(item) }
    }

    fun clearCompleted() {
        viewModelScope.launch { checklistDao.deleteCompleted() }
    }

    fun subjectName(code: String?): String? = code?.let { c -> _subjects.value.firstOrNull { it.subjectCode == c }?.subjectName }
}
