package com.uos.uosassistance.network

import com.uos.uosassistance.config.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val baseUrl: String
        get() {
            val url = AppConfig.BASE_URL
            if (url.isBlank()) return "http://localhost/"

            val formattedUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "http://$url"
            }
            return if (formattedUrl.endsWith("/")) formattedUrl else "$formattedUrl/"
        }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }
}
