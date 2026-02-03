package com.bot2u.app.data.api

import com.bot2u.app.data.model.TTSRequest
import com.bot2u.app.data.model.TTSResponse
import com.bot2u.app.data.model.VoiceUploadResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * Retrofit API service for Chatterbox TTS backend
 */
interface ChatterboxApi {
    
    /**
     * Upload a voice sample for TTS
     */
    @Multipart
    @POST("upload_voice")
    suspend fun uploadVoice(
        @Part file: okhttp3.MultipartBody.Part
    ): Response<VoiceUploadResponse>
    
    /**
     * Generate speech from text
     */
    @POST("generate")
    suspend fun generateSpeech(
        @Body request: TTSRequest
    ): Response<TTSResponse>
    
    /**
     * Clear the uploaded voice sample
     */
    @DELETE("voice")
    suspend fun clearVoice(): Response<VoiceUploadResponse>
    
    /**
     * Get audio file by filename
     */
    @GET
    suspend fun getAudio(@Url url: String): Response<ResponseBody>
    
    companion object {
        const val BASE_URL = "http://10.0.2.2:8000/" // Default FastAPI server URL
        // For physical device, replace with your computer's IP address
    }
}
