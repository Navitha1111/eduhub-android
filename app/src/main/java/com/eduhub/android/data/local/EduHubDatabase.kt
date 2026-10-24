package com.eduhub.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eduhub.android.data.local.dao.*
import com.eduhub.android.data.local.entities.*
import com.eduhub.android.data.seed.CurriculumSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CourseEntity::class,
        LessonEntity::class,
        QuizEntity::class,
        QuizQuestionEntity::class,
        ChallengeEntity::class,
        BookmarkEntity::class,
        NoteEntity::class,
        UserProgressEntity::class,
        CertificateEntity::class,
        SnippetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EduHubDatabase : RoomDatabase() {

    abstract fun courseDao(): CourseDao
    abstract fun lessonDao(): LessonDao
    abstract fun quizDao(): QuizDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun noteDao(): NoteDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun certificateDao(): CertificateDao
    abstract fun snippetDao(): SnippetDao

    companion object {
        @Volatile
        private var INSTANCE: EduHubDatabase? = null

        fun getDatabase(context: Context, applicationScope: CoroutineScope): EduHubDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduHubDatabase::class.java,
                    "eduhub_database.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(EduHubDatabaseCallback(applicationScope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class EduHubDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    CurriculumSeeder.populateDatabase(database)
                }
            }
        }
    }
}
