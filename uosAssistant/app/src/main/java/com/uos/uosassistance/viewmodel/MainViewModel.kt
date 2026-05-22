package com.uos.uosassistance.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uos.uosassistance.config.AppConfig
import com.uos.uosassistance.network.ChatRequest
import com.uos.uosassistance.network.NetworkModule
import com.uos.uosassistance.speech.SpeechManager
import com.uos.uosassistance.tts.TtsManager
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

class MainViewModel : ViewModel() {

    private val _uiState = mutableStateOf(UiState())
    val uiState: State<UiState> = _uiState

    private var speechManager: SpeechManager? = null
    private var ttsManager: TtsManager? = null
    private var deviceAndroidId: String = ""

    data class UiState(
        val isListening: Boolean = false,
        val isSpeaking: Boolean = false,
        val isProcessing: Boolean = false,
        val userText: String = "",
        val assistantText: String = "",
        val error: String? = null
    )

    // Updated to accept 3 arguments so MainActivity can safely pass down the Android ID
    fun initManagers(speech: SpeechManager, tts: TtsManager, androidId: String) {
        this.speechManager = speech
        this.ttsManager = tts
        this.deviceAndroidId = androidId
    }

    fun onMicClicked() {
        if (_uiState.value.isSpeaking) {
            ttsManager?.stop()
            return
        }

        if (_uiState.value.isListening) {
            speechManager?.stopListening()
        } else {
            _uiState.value = _uiState.value.copy(userText = "", assistantText = "", error = null)
            speechManager?.startListening()
        }
    }

    fun onSpeechResult(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(userText = text, isProcessing = true)
            sendMessageToApi(text)
        }
    }

    fun onPartialSpeechResult(text: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(userText = text)
        }
    }

    fun onSpeechError(error: String) {
        viewModelScope.launch {
            Log.d("MainViewModel", "Speech error: $error")
            _uiState.value = _uiState.value.copy(
                assistantText = error,
                error = error,
                isListening = false
            )
            ttsManager?.speak(error)
        }
    }

    fun onListeningStateChanged(isListening: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isListening = isListening)
        }
    }

    fun onSpeakingStateChanged(isSpeaking: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSpeaking = isSpeaking)
        }
    }

    private fun sendMessageToApi(text: String) {
        viewModelScope.launch {
            try {
                // Now passing deviceAndroidId securely through the request payload object
                val response = NetworkModule.apiService.sendMessage(
                    token = "Bearer ${AppConfig.API_TOKEN}",
                    request = ChatRequest(message = text, androidId = deviceAndroidId)
                )
                Log.d("MainViewModel", "API Response: ${response.response}")
                _uiState.value = _uiState.value.copy(
                    assistantText = response.response,
                    isProcessing = false
                )
                ttsManager?.speak(response.response)
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is ConnectException, is SocketTimeoutException, is UnknownHostException, is UnknownServiceException ->
                        "سر ور عارضی طور پر دستیاب نہیں ہے، براہ کرم بعد میں دوبارہ کوشش کریں۔\n"
                    else -> "API Error: ${e.localizedMessage}"
                }
                Log.e("MainViewModel", "API Error: $errorMessage", e)
                _uiState.value = _uiState.value.copy(
                    assistantText = errorMessage,
                    error = if (errorMessage == "Server is not reachable") null else errorMessage,
                    isProcessing = false
                )
                ttsManager?.speak(errorMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager?.destroy()
        ttsManager?.destroy()
    }
}