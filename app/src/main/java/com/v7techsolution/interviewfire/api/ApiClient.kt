package com.v7techsolution.interviewfire.api

import android.content.Context
import android.os.Build
import android.provider.Settings
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Dynamic base URL based on device type
    private fun getBaseUrl(context: Context): String {
        return if (isEmulator(context)) {
            "http://10.0.2.2:3000/" // For Android emulator
        } else {
            "http://192.168.31.6:3000/" // For physical device (your computer's IP)
        }
    }
    
    // Check if running on emulator
    private fun isEmulator(context: Context): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }
    
    // Create Retrofit instance with dynamic base URL
    fun createRetrofit(context: Context): Retrofit {
        val baseUrl = getBaseUrl(context)
        android.util.Log.d("ApiClient", "Using base URL: $baseUrl")
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // Legacy static instance (for backward compatibility)
    private var retrofit: Retrofit? = null
    
    val apiService: CloudInterviewApiService by lazy {
        // This will be initialized when first accessed
        // Note: You'll need to call createRetrofit() first
        throw IllegalStateException("Please call ApiClient.createRetrofit(context) first")
    }
} 