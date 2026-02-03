package com.bot2u.app.data.repository

import com.bot2u.app.data.api.ChatterboxApi
import com.bot2u.app.data.api.RetrofitClient
import com.bot2u.app.data.model.TTSRequest
import com.bot2u.app.data.model.TTSResponse
import com.bot2u.app.data.model.VoiceUploadResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * Repository for TTS operations
 */
class TTSRepository(
    private val api: ChatterboxApi = RetrofitClient.api
) {
    /**
     * Upload a voice sample for TTS
     */
    suspend fun uploadVoice(voiceFile: File): Result<VoiceUploadResponse> = withContext(Dispatchers.IO) {
        try {
            val requestBody = voiceFile.asRequestBody("audio/wav".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", voiceFile.name, requestBody)
            val response = api.uploadVoice(multipartBody)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate speech from text
     */
    suspend fun generateSpeech(request: TTSRequest): Result<TTSResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.generateSpeech(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Generation failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clear the uploaded voice sample
     */
    suspend fun clearVoice(): Result<VoiceUploadResponse> = withContext(Dispatchers.IO) {
        try {
            val response = api.clearVoice()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Clear failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Download generated audio file
     */
    suspend fun downloadAudio(audioPath: String): Result<File> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAudio(audioPath)
            
            if (response.isSuccessful && response.body() != null) {
                val tempFile = File.createTempFile("tts_audio", ".wav")
                response.body()!!.byteStream().use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                Result.success(tempFile)
            } else {
                Result.failure(Exception("Download failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
