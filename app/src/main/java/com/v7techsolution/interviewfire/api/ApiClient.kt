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

    private val firebaseAuth = FirebaseAuth.getInstance()

    // Authentication interceptor that adds Firebase ID token to requests
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val url = original.url.toString()

            // Skip authentication for static JSON files (Firebase Hosting)
            if (url.endsWith(".json")) {
                android.util.Log.d("AuthInterceptor", "Skipping authentication for static JSON file: $url")
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", "CloudInterviewApp/1.0.0 (Android)")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                return chain.proceed(requestBuilder.build())
            }

            // Get Firebase ID token for authenticated endpoints
            val idToken = getFirebaseIdToken()
            
            // Build request with authentication headers
            val requestBuilder = original.newBuilder()
                .header("User-Agent", "CloudInterviewApp/1.0.0 (Android)")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
            
            // Add Authorization header if token is available
            if (idToken != null) {
                requestBuilder.header("Authorization", "Bearer $idToken")
                android.util.Log.d("AuthInterceptor", "Added Firebase ID token to request")
            } else {
                android.util.Log.w("AuthInterceptor", "No Firebase ID token available")
            }
            
            val request = requestBuilder.build()
            return chain.proceed(request)
        }
        
        private fun getFirebaseIdToken(): String? {
            return try {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    // Get the ID token synchronously (this is a simplified approach)
                    // In production, you should use the async version
                    currentUser.getIdToken(false).result?.token
                } else {
                    null
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthInterceptor", "Error getting Firebase ID token", e)
                null
            }
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
            
            if (response.code == 429) {
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

        // Create OkHttpClient with authentication and rate limit interceptors
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
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
    
    // Refresh Firebase ID token
    fun refreshFirebaseToken(onComplete: (String?) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        android.util.Log.d("ApiClient", "Firebase ID token refreshed successfully")
                        onComplete(token)
                    } else {
                        android.util.Log.e("ApiClient", "Failed to refresh Firebase ID token", task.exception)
                        onComplete(null)
                    }
                }
        } else {
            android.util.Log.w("ApiClient", "No authenticated user found")
            onComplete(null)
        }
    }
    
    // Clear cached token (sign out)
    fun clearCachedToken() {
        firebaseAuth.signOut()
        android.util.Log.d("ApiClient", "User signed out, token cleared")
    }
    
    // Check if user is authenticated
    fun isUserAuthenticated(): Boolean {
        return firebaseAuth.currentUser != null
    }
    
    // Get current user
    fun getCurrentUser() = firebaseAuth.currentUser
    
    // Legacy static instance (for backward compatibility)
    private var retrofit: Retrofit? = null
    
    val apiService: CloudInterviewApiService by lazy {
        // This will be initialized when first accessed
        // Note: You'll need to call createRetrofit() first
        throw IllegalStateException("Please call ApiClient.createRetrofit(context) first")
    }
} 