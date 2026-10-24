package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.SnippetEntity
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.domain.models.CodeExecutionResult
import com.eduhub.android.utils.CodeEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaygroundViewModel(private val repository: EduHubRepository) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("python")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _codeBuffer = MutableStateFlow("")
    val codeBuffer: StateFlow<String> = _codeBuffer.asStateFlow()

    private val _terminalOutput = MutableStateFlow<CodeExecutionResult?>(null)
    val terminalOutput: StateFlow<CodeExecutionResult?> = _terminalOutput.asStateFlow()

    private val _fontSize = MutableStateFlow(13)
    val fontSize: StateFlow<Int> = _fontSize.asStateFlow()

    init {
        setLanguage("python")
    }

    fun setLanguage(slug: String) {
        _selectedLanguage.value = slug
        _codeBuffer.value = CodeEvaluator.getStarterCodeForLanguage(slug)
        _terminalOutput.value = null
    }

    fun updateCode(code: String) {
        _codeBuffer.value = code
    }

    fun insertSymbol(symbol: String) {
        _codeBuffer.value = _codeBuffer.value + symbol
    }

    fun runCode() {
        val result = CodeEvaluator.evaluateCode(
            languageSlug = _selectedLanguage.value,
            userCode = _codeBuffer.value
        )
        _terminalOutput.value = result
    }

    fun clearTerminal() {
        _terminalOutput.value = null
    }

    fun saveAsSnippet(title: String) {
        val lang = _selectedLanguage.value
        val code = _codeBuffer.value
        if (title.isNotBlank() && code.isNotBlank()) {
            viewModelScope.launch {
                repository.saveSnippet(
                    title = title,
                    languageSlug = lang,
                    languageName = lang.replaceFirstChar { it.uppercase() },
                    code = code,
                    tags = "$lang,playground"
                )
            }
        }
    }
}
