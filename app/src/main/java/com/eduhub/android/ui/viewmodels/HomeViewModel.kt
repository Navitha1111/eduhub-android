package com.eduhub.android.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduhub.android.data.local.entities.CourseEntity
import com.eduhub.android.data.local.entities.UserProgressEntity
import com.eduhub.android.data.repository.EduHubRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EduHubRepository) : ViewModel() {

    val userProgress: StateFlow<UserProgressEntity?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val inProgressCourses: StateFlow<List<CourseEntity>> = repository.inProgressCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCourses: StateFlow<List<CourseEntity>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.checkDailyStreak()
        }
    }
}
