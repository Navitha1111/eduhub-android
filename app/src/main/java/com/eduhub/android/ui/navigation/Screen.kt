package com.eduhub.android.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Courses : Screen("courses")
    object CourseDetail : Screen("course/{slug}") {
        fun createRoute(slug: String) = "course/$slug"
    }
    object LessonDetail : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: Long) = "lesson/$lessonId"
    }
    object Quiz : Screen("quiz/{courseSlug}") {
        fun createRoute(courseSlug: String) = "quiz/$courseSlug"
    }
    object Challenge : Screen("challenge/{courseSlug}") {
        fun createRoute(courseSlug: String) = "challenge/$courseSlug"
    }
    object Playground : Screen("playground?lang={lang}") {
        fun createRoute(lang: String = "python") = "playground?lang=$lang"
    }
    object CheatSheet : Screen("cheatsheet")
    object Flashcard : Screen("flashcard")
    object BookmarksNotes : Screen("bookmarks_notes")
    object Certificates : Screen("certificates")
    object AdminCourseCreator : Screen("admin_creator")
    object Profile : Screen("profile")
    object Auth : Screen("auth")
}
