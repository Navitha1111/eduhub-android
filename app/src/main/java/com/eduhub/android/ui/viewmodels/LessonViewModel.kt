package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.LessonEntity
import com.eduhub.android.data.local.entities.NoteEntity
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.utils.TextToSpeechManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LessonViewModel(
    private val repository: EduHubRepository,
    private val ttsManager: TextToSpeechManager
) : ViewModel() {

    private val _lesson = MutableStateFlow<LessonEntity?>(null)
    val lesson: StateFlow<LessonEntity?> = _lesson.asStateFlow()

    private val _note = MutableStateFlow<NoteEntity?>(null)
    val note: StateFlow<NoteEntity?> = _note.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking
    val speechRate: StateFlow<Float> = ttsManager.speechRate

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    fun loadLesson(lessonId: Long) {
        viewModelScope.launch {
            repository.getLessonById(lessonId).collect { l ->
                _lesson.value = l
                _isCompleted.value = l?.isCompleted ?: false
            }
        }
        viewModelScope.launch {
            repository.getNoteForLesson(lessonId).collect { n ->
                _note.value = n
            }
        }
    }

    fun completeCurrentLesson() {
        val current = _lesson.value ?: return
        viewModelScope.launch {
            repository.completeLesson(current.id, current.courseId)
            _isCompleted.value = true
        }
    }

    fun toggleBookmark() {
        val current = _lesson.value ?: return
        viewModelScope.launch {
            repository.toggleBookmark(
                lessonId = current.id,
                courseSlug = current.courseSlug,
                courseTitle = current.courseTitle,
                lessonTitle = current.title,
                moduleTitle = current.moduleTitle
            )
        }
    }

    fun saveNote(content: String) {
        val current = _lesson.value ?: return
        viewModelScope.launch {
            repository.saveNote(
                lessonId = current.id,
                courseSlug = current.courseSlug,
                courseTitle = current.courseTitle,
                lessonTitle = current.title,
                content = content
            )
        }
    }

    fun playAudioNarration() {
        val current = _lesson.value ?: return
        val narrationText = "${current.title}. Objectives: ${current.objectives}. Content: ${current.contentMarkdown}"
        ttsManager.speak(narrationText)
    }

    fun stopAudioNarration() {
        ttsManager.stop()
    }

    fun cycleSpeechRate() {
        val current = ttsManager.speechRate.value
        val next = when (current) {
            0.75f -> 1.0f
            1.0f -> 1.25f
            1.25f -> 1.5f
            else -> 0.75f
        }
        ttsManager.setRate(next)
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }
}
