package com.uos.uosassistance.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    // Backend par controller ka path /agent hi hai
    @POST("agent")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): ChatResponse
}