package com.uos.uosassistance.network

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("message") val message: String,
    @SerializedName("android_id") val androidId: String // Added for multi-user mapping
)

data class ChatResponse(
    @SerializedName("response") val response: String
)