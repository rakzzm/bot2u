package com.bot2u.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for TTS generation
 */
data class TTSRequest(
    @SerializedName("text")
    val text: String,
    
    @SerializedName("exaggeration")
    val exaggeration: Float = 0.5f,
    
    @SerializedName("cfg_weight")
    val cfgWeight: Float = 0.5f,
    
    @SerializedName("temperature")
    val temperature: Float = 0.8f,
    
    @SerializedName("repetition_penalty")
    val repetitionPenalty: Float = 1.2f,
    
    @SerializedName("top_p")
    val topP: Float = 1.0f,
    
    @SerializedName("min_p")
    val minP: Float = 0.05f
)

/**
 * Response model for TTS generation
 */
data class TTSResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("audio_path")
    val audioPath: String?,
    
    @SerializedName("message")
    val message: String?
)

/**
 * Response model for voice upload
 */
data class VoiceUploadResponse(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String?
)
