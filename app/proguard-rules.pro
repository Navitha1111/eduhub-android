# ======================================================================
# EDUHUB ANDROID — PRODUCTION PROGUARD / R8 RELEASE RULES
# ======================================================================

# 1. Room Database & SQLite Architecture
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep class com.eduhub.android.data.local.** { *; }
-keep class com.eduhub.android.data.local.dao.** { *; }
-keep class com.eduhub.android.data.local.entities.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }

# 2. Domain Models & Curriculum Data Catalog
-keep class com.eduhub.android.domain.models.** { *; }
-keep class com.eduhub.android.data.seed.** { *; }
-keep class com.eduhub.android.data.repository.** { *; }

# 3. Android Jetpack Compose & Navigation
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class androidx.navigation.** { *; }
-keep class com.eduhub.android.ui.navigation.** { *; }
-keep class com.eduhub.android.ui.viewmodels.** { *; }

# 4. Kotlin Coroutines & Flow
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# 5. TextToSpeech & Native System Services
-keep class android.speech.tts.** { *; }
-keep class com.eduhub.android.utils.** { *; }

# 6. Global Resilience for Android Third-Party Libraries
-ignorewarnings
-dontwarn **
