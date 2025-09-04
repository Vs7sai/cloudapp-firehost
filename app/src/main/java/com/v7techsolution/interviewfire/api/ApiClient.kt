package com.v7techsolution.interviewfire.api

import android.content.Context
import android.os.Build
import android.provider.Settings
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object ApiClient {

    // No token management needed for static JSON API

    // Simple interceptor for static JSON API (no authentication headers needed)
    private class StaticApiInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            // Build request with basic headers for static JSON API
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "CloudInterviewApp/1.0.0 (Android)")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
            
            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }

    // Rate limit interceptor to handle rate limiting responses
    private class RateLimitInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            
            // Check for rate limit headers
            val rateLimitRemaining = response.header("X-RateLimit-Remaining")
            val rateLimitReset = response.header("X-RateLimit-Reset")
            val retryAfter = response.header("Retry-After")
            
            if (rateLimitRemaining != null) {
                android.util.Log.d("RateLimitInterceptor", "Rate limit remaining: $rateLimitRemaining")
            }
            
            if (response.code() == 429) {
                android.util.Log.w("RateLimitInterceptor", "Rate limit exceeded. Retry after: $retryAfter seconds")
            }
            
            return response
        }
    }

    // Firebase Hosting URL (static JSON API)
    private fun getBaseUrl(context: Context): String {
      // Using Firebase Hosting URL for static JSON API
      return "https://interviewfire-df24e.web.app/"
    }
    
    // Firebase Hosting URL for authentication (same as main URL)
    private fun getDirectBaseUrl(context: Context): String {
      // Use Firebase Hosting URL for authentication
      return "https://interviewfire-df24e.web.app/"
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
    
    // Create Retrofit instance with dynamic base URL and authentication
    fun createRetrofit(context: Context): Retrofit {
        val baseUrl = getBaseUrl(context)
        android.util.Log.d("ApiClient", "🔗 Using base URL: $baseUrl")
        android.util.Log.d("ApiClient", "🌐 Full API endpoint will be: ${baseUrl}topics")

        // Create OkHttpClient with static API and rate limit interceptors
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(StaticApiInterceptor())
            .addInterceptor(RateLimitInterceptor())
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    // No authentication required for static JSON API
    fun refreshFirebaseToken(onComplete: (String?) -> Unit) {
        // Static JSON API doesn't require authentication
        android.util.Log.d("ApiClient", "No authentication required for static JSON API")
        onComplete("no-auth-required")
    }
    
    // No token management needed for static JSON API
    fun clearCachedToken() {
        android.util.Log.d("ApiClient", "No token management needed for static JSON API")
    }
    
    // Legacy static instance (for backward compatibility)
    private var retrofit: Retrofit? = null
    
    val apiService: CloudInterviewApiService by lazy {
        // This will be initialized when first accessed
        // Note: You'll need to call createRetrofit() first
        throw IllegalStateException("Please call ApiClient.createRetrofit(context) first")
    }
} 