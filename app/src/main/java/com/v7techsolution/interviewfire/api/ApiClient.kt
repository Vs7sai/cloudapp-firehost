package com.v7techsolution.interviewfire.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:3000/" // For Android emulator
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: CloudInterviewApiService by lazy {
        retrofit.create(CloudInterviewApiService::class.java)
    }
} 