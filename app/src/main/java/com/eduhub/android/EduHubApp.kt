package com.eduhub.android

import android.app.Application
import com.eduhub.android.data.local.EduHubDatabase
import com.eduhub.android.data.repository.EduHubRepository
import com.eduhub.android.utils.TextToSpeechManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class EduHubApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EduHubDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { EduHubRepository(database) }
    val ttsManager by lazy { TextToSpeechManager(this) }

    override fun onTerminate() {
        super.onTerminate()
        ttsManager.shutdown()
    }
}
