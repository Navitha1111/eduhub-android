package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.BookmarkEntity
import com.eduhub.android.data.local.entities.CertificateEntity
import com.eduhub.android.data.local.entities.NoteEntity
import com.eduhub.android.data.local.entities.UserProgressEntity
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.data.seed.LanguageCatalogData
import com.eduhub.android.data.seed.LanguageSpec
import com.eduhub.android.domain.models.Flashcard
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 1. CheatSheetViewModel
class CheatSheetViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allSpecs = MutableStateFlow(LanguageCatalogData.languages)

    val filteredSpecs: StateFlow<List<LanguageSpec>> = combine(_allSpecs, _searchQuery) { list, q ->
        if (q.isEmpty()) list
        else list.filter {
            it.langName.contains(q, ignoreCase = true) ||
            it.cheatSheetSummary.contains(q, ignoreCase = true) ||
            it.category.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LanguageCatalogData.languages)

    fun updateSearch(q: String) { _searchQuery.value = q }
}

// 2. FlashcardViewModel
class FlashcardViewModel : ViewModel() {
    private val _cards = MutableStateFlow<List<Flashcard>>(
        LanguageCatalogData.languages.flatMapIndexed { index, spec ->
            listOf(
                Flashcard(
                    id = (index * 2 + 1).toLong(),
                    languageSlug = spec.slug,
                    languageName = spec.langName,
                    question = "What is the core paradigm of ${spec.langName}?",
                    answer = spec.paradigm,
                    category = spec.category
                ),
                Flashcard(
                    id = (index * 2 + 2).toLong(),
                    languageSlug = spec.slug,
                    languageName = spec.langName,
                    question = "How is basic syntax and output structured in ${spec.langName}?",
                    answer = spec.helloCode,
                    category = spec.category
                )
            )
        }
    )

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped.asStateFlow()

    val currentCard: StateFlow<Flashcard?> = combine(_cards, _currentIndex) { list, idx ->
        if (list.isNotEmpty() && idx < list.size) list[idx] else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun flipCard() { _isFlipped.value = !_isFlipped.value }

    fun nextCard() {
        if (_currentIndex.value + 1 < _cards.value.size) {
            _currentIndex.value += 1
            _isFlipped.value = false
        } else {
            _currentIndex.value = 0
            _isFlipped.value = false
        }
    }

    fun prevCard() {
        if (_currentIndex.value > 0) {
            _currentIndex.value -= 1
            _isFlipped.value = false
        }
    }
}

// 3. CertificateViewModel
class CertificateViewModel(private val repository: EduHubRepository) : ViewModel() {
    val certificates: StateFlow<List<CertificateEntity>> = repository.allCertificates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _verificationResult = MutableStateFlow<com.eduhub.android.data.remote.CertificateVerificationData?>(null)
    val verificationResult: StateFlow<com.eduhub.android.data.remote.CertificateVerificationData?> = _verificationResult.asStateFlow()

    private val _isVerifying = MutableStateFlow(false)
    val isVerifying: StateFlow<Boolean> = _isVerifying.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _claimMessage = MutableStateFlow<String?>(null)
    val claimMessage: StateFlow<String?> = _claimMessage.asStateFlow()

    fun syncCertificates() {
        viewModelScope.launch {
            _isSyncing.value = true
            val count = repository.syncCloudUserProgressAndCertificates()
            _isSyncing.value = false
            if (count > 0) {
                _claimMessage.value = "✓ Synced $count official certificates with cloud account!"
            }
        }
    }

    fun claimCertificateForCourse(slug: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            val cert = repository.claimCourseCertificate(slug)
            _isSyncing.value = false
            if (cert != null) {
                _claimMessage.value = "🎓 Certificate for ${cert.courseTitle} successfully added to your wallet!"
            }
        }
    }

    fun verifyCertificate(code: String) {
        if (code.isBlank()) return
        viewModelScope.launch {
            _isVerifying.value = true
            _verificationResult.value = repository.verifyCertificateOnline(code)
            _isVerifying.value = false
        }
    }

    fun clearVerification() {
        _verificationResult.value = null
    }

    fun clearClaimMessage() {
        _claimMessage.value = null
    }
}

// 4. BookmarksNotesViewModel
class BookmarksNotesViewModel(private val repository: EduHubRepository) : ViewModel() {
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { repository.deleteNote(note) }
    }
}

// 5. AdminCourseCreatorViewModel
class AdminCourseCreatorViewModel(private val repository: EduHubRepository) : ViewModel() {
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun autoGenerateCourse(techName: String) {
        if (techName.isBlank()) return
        viewModelScope.launch {
            val title = "$techName Complete Mastery (0 to Advanced)"
            val desc = "Automated complete 4-tier 0-to-Advanced course covering fundamentals, syntax, control flow, modularity, and capstone architecture for $techName."
            val starterCode = "// Auto-generated $techName curriculum starter\nprint('Hello from $techName!');"

            repository.createCustomCourse(
                title = title,
                languageName = techName,
                category = "Emerging & Specialized",
                difficulty = "Beginner",
                hours = 18,
                description = desc,
                starterCode = starterCode
            )
            _statusMessage.value = "⚡ Successfully created complete 4-tier curriculum for $techName!"
        }
    }

    fun clearStatus() { _statusMessage.value = null }
}

// 6. ProfileViewModel
class ProfileViewModel(private val repository: EduHubRepository) : ViewModel() {
    val userProgress: StateFlow<UserProgressEntity?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun updateName(newName: String) {
        if (newName.isNotBlank()) {
            viewModelScope.launch { repository.updateStudentName(newName) }
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch { repository.toggleTheme(isDark) }
    }

    fun updateSpeechRate(rate: Float) {
        viewModelScope.launch { repository.updateSpeechRate(rate) }
    }

    fun syncWithVps() {
        viewModelScope.launch {
            _isSyncing.value = true
            val restoredCount = repository.syncCloudUserProgressAndCertificates()
            val ping = repository.syncWithVps()
            _isSyncing.value = false
            _syncMessage.value = if (restoredCount > 0) {
                "✓ Cloud account synced! Restored $restoredCount official certificate(s) & completed lessons."
            } else if (ping.ok) {
                "✓ Progress & certificates synchronized with VPS successfully!"
            } else {
                "⚠️ Sync notice: ${ping.rawMessage}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _syncMessage.value = "Logged out from VPS account."
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }
}
