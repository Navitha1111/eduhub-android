package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.QuizEntity
import com.eduhub.android.data.local.entities.QuizQuestionEntity
import com.eduhub.android.data.repository.EduHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(private val repository: EduHubRepository) : ViewModel() {

    private val _quiz = MutableStateFlow<QuizEntity?>(null)
    val quiz: StateFlow<QuizEntity?> = _quiz.asStateFlow()

    private val _questions = MutableStateFlow<List<QuizQuestionEntity>>(emptyList())
    val questions: StateFlow<List<QuizQuestionEntity>> = _questions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted: StateFlow<Boolean> = _isAnswerSubmitted.asStateFlow()

    private val _correctCount = MutableStateFlow(0)
    val correctCount: StateFlow<Int> = _correctCount.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    fun loadQuiz(courseSlug: String) {
        viewModelScope.launch {
            val q = repository.getQuizForCourse(courseSlug)
            _quiz.value = q
            if (q != null) {
                val qList = repository.getQuizQuestions(q.id)
                _questions.value = qList
                _currentQuestionIndex.value = 0
                _selectedOption.value = null
                _isAnswerSubmitted.value = false
                _correctCount.value = 0
                _isQuizFinished.value = false
            }
        }
    }

    fun selectOption(optionIndex: Int) {
        if (!_isAnswerSubmitted.value) {
            _selectedOption.value = optionIndex
        }
    }

    fun submitAnswer() {
        val qList = _questions.value
        val currentIndex = _currentQuestionIndex.value
        val selected = _selectedOption.value ?: return

        if (currentIndex < qList.size) {
            val currentQ = qList[currentIndex]
            if (selected == currentQ.correctOptionIndex) {
                _correctCount.value += 1
            }
            _isAnswerSubmitted.value = true
        }
    }

    fun nextQuestion() {
        val qList = _questions.value
        val currentIndex = _currentQuestionIndex.value

        if (currentIndex + 1 < qList.size) {
            _currentQuestionIndex.value += 1
            _selectedOption.value = null
            _isAnswerSubmitted.value = false
        } else {
            // Finish Quiz
            _isQuizFinished.value = true
            val quizEntity = _quiz.value
            if (quizEntity != null && qList.isNotEmpty()) {
                val scorePct = ((_correctCount.value.toDouble() / qList.size) * 100).toInt()
                viewModelScope.launch {
                    repository.recordQuizScore(quizEntity.id, quizEntity.courseSlug, scorePct)
                }
            }
        }
    }

    fun restartQuiz() {
        _currentQuestionIndex.value = 0
        _selectedOption.value = null
        _isAnswerSubmitted.value = false
        _correctCount.value = 0
        _isQuizFinished.value = false
    }
}
