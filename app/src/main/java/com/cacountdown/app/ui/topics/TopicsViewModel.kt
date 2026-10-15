package com.cacountdown.app.ui.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cacountdown.app.data.local.entity.ChapterEntity
import com.cacountdown.app.data.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    val subjectCode: String = checkNotNull(savedStateHandle["subjectCode"])

    val chapters: StateFlow<List<ChapterEntity>> = progressRepository.observeChapters(subjectCode)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggle(chapter: ChapterEntity) {
        viewModelScope.launch { progressRepository.toggleChapter(chapter) }
    }

    fun addTopic(title: String) {
        viewModelScope.launch { progressRepository.addChapter(subjectCode, title) }
    }

    fun deleteTopic(chapter: ChapterEntity) {
        viewModelScope.launch { progressRepository.deleteChapter(chapter) }
    }
}
