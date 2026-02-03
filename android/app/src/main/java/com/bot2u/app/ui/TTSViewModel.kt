package com.bot2u.app.ui

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bot2u.app.data.model.TTSRequest
import com.bot2u.app.data.repository.TTSRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/**
 * UI State for the TTS screen
 */
data class TTSUiState(
    val textInput: String = "",
    val isRecording: Boolean = false,
    val isUploading: Boolean = false,
    val isGenerating: Boolean = false,
    val isPlaying: Boolean = false,
    val hasVoiceSample: Boolean = false,
    val hasGeneratedAudio: Boolean = false,
    val exaggeration: Float = 0.5f,
    val temperature: Float = 0.8f,
    val cfgWeight: Float = 0.5f,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val generatedAudioFile: File? = null
)

/**
 * ViewModel for TTS functionality
 */
class TTSViewModel(
    private val repository: TTSRepository = TTSRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TTSUiState())
    val uiState: StateFlow<TTSUiState> = _uiState.asStateFlow()
    
    private var mediaPlayer: MediaPlayer? = null
    
    fun updateTextInput(text: String) {
        _uiState.update { it.copy(textInput = text) }
    }
    
    fun updateExaggeration(value: Float) {
        _uiState.update { it.copy(exaggeration = value) }
    }
    
    fun updateTemperature(value: Float) {
        _uiState.update { it.copy(temperature = value) }
    }
    
    fun updateCfgWeight(value: Float) {
        _uiState.update { it.copy(cfgWeight = value) }
    }
    
    fun uploadVoiceSample(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUploading = true, errorMessage = null) }
            
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "voice_sample.wav")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                repository.uploadVoice(tempFile)
                    .onSuccess { response ->
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                hasVoiceSample = true,
                                successMessage = response.message
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isUploading = false,
                                errorMessage = error.message ?: "Failed to upload voice sample"
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isUploading = false,
                        errorMessage = e.message ?: "Failed to process voice file"
                    )
                }
            }
        }
    }
    
    fun generateSpeech() {
        val currentState = _uiState.value
        
        if (!currentState.hasVoiceSample) {
            _uiState.update { it.copy(errorMessage = "Please upload a voice sample first") }
            return
        }
        
        if (currentState.textInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter some text to synthesize") }
            return
        }
        
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, errorMessage = null) }
            
            val request = TTSRequest(
                text = currentState.textInput,
                exaggeration = currentState.exaggeration,
                temperature = currentState.temperature,
                cfgWeight = currentState.cfgWeight
            )
            
            repository.generateSpeech(request)
                .onSuccess { response ->
                    if (response.success && response.audioPath != null) {
                        repository.downloadAudio(response.audioPath)
                            .onSuccess { audioFile ->
                                _uiState.update {
                                    it.copy(
                                        isGenerating = false,
                                        hasGeneratedAudio = true,
                                        generatedAudioFile = audioFile,
                                        successMessage = "Speech generated successfully!"
                                    )
                                }
                            }
                            .onFailure { error ->
                                _uiState.update {
                                    it.copy(
                                        isGenerating = false,
                                        errorMessage = error.message ?: "Failed to download audio"
                                    )
                                }
                            }
                    } else {
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                errorMessage = response.message ?: "Failed to generate speech"
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            errorMessage = error.message ?: "Failed to generate speech"
                        )
                    }
                }
        }
    }
    
    fun playAudio() {
        val audioFile = _uiState.value.generatedAudioFile ?: return
        
        try {
            stopAudio() // Stop any existing playback
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()
                setOnCompletionListener {
                    _uiState.update { it.copy(isPlaying = false) }
                }
                start()
            }
            _uiState.update { it.copy(isPlaying = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = "Failed to play audio: ${e.message}") }
        }
    }
    
    fun stopAudio() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        _uiState.update { it.copy(isPlaying = false) }
    }
    
    fun clearVoiceSample() {
        viewModelScope.launch {
            repository.clearVoice()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            hasVoiceSample = false,
                            hasGeneratedAudio = false,
                            generatedAudioFile = null,
                            successMessage = "Voice sample cleared"
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message) }
                }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}
