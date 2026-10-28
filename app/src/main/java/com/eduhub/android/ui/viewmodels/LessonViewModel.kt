package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.LessonEntity
import com.eduhub.android.data.local.entities.NoteEntity
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.utils.CodeEvaluator
import com.eduhub.android.utils.TextToSpeechManager
import kotlinx.coroutines.delay
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

    // --- Interactive In-Lesson Code Studio State ---
    private val _editableCode = MutableStateFlow("")
    val editableCode: StateFlow<String> = _editableCode.asStateFlow()

    private val _runOutput = MutableStateFlow<String?>(null)
    val runOutput: StateFlow<String?> = _runOutput.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    // --- Navigation & Course Stepper State ---
    private val _prevLessonId = MutableStateFlow<Long?>(null)
    val prevLessonId: StateFlow<Long?> = _prevLessonId.asStateFlow()

    private val _nextLessonId = MutableStateFlow<Long?>(null)
    val nextLessonId: StateFlow<Long?> = _nextLessonId.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _totalLessons = MutableStateFlow(12)
    val totalLessons: StateFlow<Int> = _totalLessons.asStateFlow()

    // --- Hands-On Mini-Challenge Solution State ---
    private val _showExerciseSolution = MutableStateFlow(false)
    val showExerciseSolution: StateFlow<Boolean> = _showExerciseSolution.asStateFlow()

    fun loadLesson(lessonId: Long) {
        viewModelScope.launch {
            repository.getLessonById(lessonId).collect { l ->
                _lesson.value = l
                _isCompleted.value = l?.isCompleted ?: false
                if (l != null) {
                    if (_editableCode.value.isEmpty() || _lesson.value?.id != l.id) {
                        _editableCode.value = l.codeExample
                    }
                    _runOutput.value = null
                    _showExerciseSolution.value = false

                    // Load neighboring lessons for course stepper
                    repository.getLessonsForCourse(l.courseSlug).collect { list ->
                        if (list.isNotEmpty()) {
                            _totalLessons.value = list.size
                            val idx = list.indexOfFirst { it.id == l.id }
                            if (idx >= 0) {
                                _currentIndex.value = idx
                                _prevLessonId.value = list.getOrNull(idx - 1)?.id
                                _nextLessonId.value = list.getOrNull(idx + 1)?.id
                            }
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.getNoteForLesson(lessonId).collect { n ->
                _note.value = n
            }
        }
    }

    fun onCodeChanged(newCode: String) {
        _editableCode.value = newCode
    }

    fun insertSymbol(symbol: String) {
        _editableCode.value = _editableCode.value + symbol
    }

    fun resetCode() {
        val current = _lesson.value ?: return
        _editableCode.value = current.codeExample
        _runOutput.value = null
    }

    fun runCode() {
        val current = _lesson.value ?: return
        val codeToRun = _editableCode.value
        _isExecuting.value = true
        viewModelScope.launch {
            delay(150)
            val result = CodeEvaluator.evaluateCode(current.courseSlug, codeToRun)
            _runOutput.value = result.output
            _isExecuting.value = false
        }
    }

    fun toggleExerciseSolution() {
        _showExerciseSolution.value = !_showExerciseSolution.value
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
