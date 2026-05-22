package com.uos.uosassistance.tts

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.uos.uosassistance.config.AppConfig
import java.util.Locale

class TtsManager(
    context: Context,
    private val onStateChanged: (Boolean) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null

    init {
        tts = TextToSpeech(context, this, "com.google.android.tts")
        if (tts == null) {
            tts = TextToSpeech(context, this)
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        tts?.setAudioAttributes(audioAttributes)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val localePK = Locale("ur", "PK")
            val localeGeneric = Locale("ur")
            
            var result = tts?.setLanguage(localePK)
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TtsManager", "Urdu (PK) not supported, trying generic Urdu")
                result = tts?.setLanguage(localeGeneric)
            }
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "Urdu language is not supported or missing data even with fallback")
                isInitialized = false
            } else {
                Log.d("TtsManager", "TTS Initialized successfully")
                isInitialized = true
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        onStateChanged(true)
                    }

                    override fun onDone(utteranceId: String?) {
                        onStateChanged(false)
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("TtsManager", "Utterance error: $utteranceId")
                        onStateChanged(false)
                    }
                })
                
                // Speak pending text if any
                pendingText?.let {
                    speak(it)
                    pendingText = null
                }
            }
        } else {
            Log.e("TtsManager", "TTS Initialization failed with status: $status")
            isInitialized = false
        }
    }

    fun speak(text: String) {
        if (isInitialized) {
            Log.d("TtsManager", "Speaking: $text")
            val result = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "UtteranceId")
            if (result == TextToSpeech.ERROR) {
                Log.e("TtsManager", "Error calling tts.speak")
            }
        } else {
            Log.w("TtsManager", "TTS not initialized yet, queueing text: $text")
            pendingText = text
        }
    }

    fun stop() {
        tts?.stop()
        onStateChanged(false)
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
