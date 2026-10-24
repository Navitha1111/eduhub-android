package com.eduhub.android.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class TextToSpeechManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = true
                    engine.setSpeechRate(_speechRate.value)
                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })
                }
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized) return
        stop()
        val cleanText = text
            .replace("#", "")
            .replace("*", "")
            .replace("`", "")
            .replace("•", "bullet point:")
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "EDU_SPEECH_ID")
        _isSpeaking.value = true
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
            _isSpeaking.value = false
        }
    }

    fun setRate(rate: Float) {
        _speechRate.value = rate
        if (isInitialized) {
            tts?.setSpeechRate(rate)
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        _isSpeaking.value = false
    }
}
