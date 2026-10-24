package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.ChallengeEntity
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.domain.models.CodeExecutionResult
import com.eduhub.android.utils.CodeEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChallengeViewModel(private val repository: EduHubRepository) : ViewModel() {

    private val _challenge = MutableStateFlow<ChallengeEntity?>(null)
    val challenge: StateFlow<ChallengeEntity?> = _challenge.asStateFlow()

    private val _userCode = MutableStateFlow("")
    val userCode: StateFlow<String> = _userCode.asStateFlow()

    private val _executionResult = MutableStateFlow<CodeExecutionResult?>(null)
    val executionResult: StateFlow<CodeExecutionResult?> = _executionResult.asStateFlow()

    private val _showHint = MutableStateFlow(false)
    val showHint: StateFlow<Boolean> = _showHint.asStateFlow()

    private val _isSolved = MutableStateFlow(false)
    val isSolved: StateFlow<Boolean> = _isSolved.asStateFlow()

    fun loadChallenge(courseSlug: String) {
        viewModelScope.launch {
            val c = repository.getChallengeForCourse(courseSlug)
            _challenge.value = c
            if (c != null) {
                _userCode.value = c.starterCode
                _isSolved.value = c.isSolved
                _executionResult.value = null
                _showHint.value = false
            }
        }
    }

    fun updateCode(newCode: String) {
        _userCode.value = newCode
    }

    fun toggleHint() {
        _showHint.value = !_showHint.value
    }

    fun resetStarter() {
        _challenge.value?.let {
            _userCode.value = it.starterCode
            _executionResult.value = null
        }
    }

    fun runAndVerifyCode() {
        val ch = _challenge.value ?: return
        val result = CodeEvaluator.evaluateCode(
            languageSlug = ch.courseSlug,
            userCode = _userCode.value,
            rubricPatterns = ch.rubricPatterns
        )
        _executionResult.value = result

        if (result.isSuccess && !_isSolved.value) {
            _isSolved.value = true
            viewModelScope.launch {
                repository.solveChallenge(ch.id, ch.earnedXp)
            }
        }
    }
}
