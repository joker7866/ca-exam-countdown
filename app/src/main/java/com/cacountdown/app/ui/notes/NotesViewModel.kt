package com.cacountdown.app.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.dao.NoteDao
import com.cacountdown.app.data.local.entity.NoteEntity
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
class NotesViewModel @Inject constructor(
    private val noteDao: NoteDao,
    private val subjectOptionsSource: SubjectOptionsSource,
) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = noteDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _subjects = MutableStateFlow<List<Exam>>(emptyList())
    val subjects: StateFlow<List<Exam>> = _subjects

    init {
        viewModelScope.launch { _subjects.value = subjectOptionsSource.current() }
    }

    fun addNote(title: String, body: String, subjectCode: String?) {
        if (title.isBlank() && body.isBlank()) return
        viewModelScope.launch {
            val now = Instant.now()
            noteDao.insert(
                NoteEntity(
                    subjectCode = subjectCode,
                    title = title.ifBlank { "Untitled note" },
                    body = body,
                    createdAt = now,
                    updatedAt = now,
                ),
            )
        }
    }

    fun updateNote(note: NoteEntity, title: String, body: String) {
        viewModelScope.launch {
            noteDao.update(note.copy(title = title.ifBlank { "Untitled note" }, body = body, updatedAt = Instant.now()))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { noteDao.delete(note) }
    }

    fun subjectName(code: String?): String? = code?.let { c -> _subjects.value.firstOrNull { it.subjectCode == c }?.subjectName }
}
