package com.uos.uosassistance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings // Imported to retrieve the unique device Android ID
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import com.uos.uosassistance.speech.SpeechManager
import com.uos.uosassistance.tts.TtsManager
import com.uos.uosassistance.ui.MainScreen
import com.uos.uosassistance.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initManagers()

        setContent {
            MaterialTheme {
                MainScreen(viewModel)
            }
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun initManagers() {
        // Securely fetches the single execution space unique Android identifier
        val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"

        val speechManager = SpeechManager(
            context = this,
            onResult = { viewModel.onSpeechResult(it) },
            onPartialResult = { viewModel.onPartialSpeechResult(it) },
            onError = { viewModel.onSpeechError(it) },
            onListeningStateChanged = { viewModel.onListeningStateChanged(it) }
        )

        val ttsManager = TtsManager(
            context = this,
            onStateChanged = { viewModel.onSpeakingStateChanged(it) }
        )

        // Forward the extracted androidId securely into your MainViewModel initialization
        viewModel.initManagers(speechManager, ttsManager, androidId)
    }
}