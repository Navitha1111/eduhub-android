package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.CourseEntity
import com.eduhub.android.data.local.entities.LessonEntity
import com.eduhub.android.data.local.entities.QuizEntity
import com.eduhub.android.data.local.entities.ChallengeEntity
import com.eduhub.android.data.repository.EduHubRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: EduHubRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All Categories")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow("All Levels")
    val selectedDifficulty: StateFlow<String> = _selectedDifficulty.asStateFlow()

    val filteredCourses: StateFlow<List<CourseEntity>> = combine(
        repository.allCourses,
        _searchQuery,
        _selectedCategory,
        _selectedDifficulty
    ) { courses, query, category, difficulty ->
        courses.filter { course ->
            val matchesQuery = query.isEmpty() ||
                course.title.contains(query, ignoreCase = true) ||
                course.languageName.contains(query, ignoreCase = true) ||
                course.description.contains(query, ignoreCase = true)

            val matchesCategory = category == "All Categories" ||
                course.category.equals(category, ignoreCase = true)

            val matchesDifficulty = difficulty == "All Levels" ||
                course.difficulty.equals(difficulty, ignoreCase = true)

            matchesQuery && matchesCategory && matchesDifficulty
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectDifficulty(difficulty: String) {
        _selectedDifficulty.value = difficulty
    }

    // Detail Screen Methods
    fun getCourse(slug: String): StateFlow<CourseEntity?> {
        return repository.getCourseBySlug(slug)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun getLessons(slug: String): StateFlow<List<LessonEntity>> {
        return repository.getLessonsForCourse(slug)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getQuiz(slug: String): StateFlow<QuizEntity?> {
        return repository.getQuizForCourseFlow(slug)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }

    fun getChallenge(slug: String): StateFlow<ChallengeEntity?> {
        return repository.getChallengeForCourseFlow(slug)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    }
}
