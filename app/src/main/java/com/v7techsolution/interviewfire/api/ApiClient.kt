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

    // Token manager to handle Firebase ID tokens
    private object TokenManager {
        private var currentToken: String? = null
        private var tokenExpiry: Long = 0

        fun getToken(): String? {
            // Check if token is expired (tokens expire after 1 hour)
            if (currentToken != null && System.currentTimeMillis() < tokenExpiry) {
                return currentToken
            }
            return null
        }

        fun setToken(token: String) {
            currentToken = token
            // Set expiry to 50 minutes from now (tokens last 1 hour, refresh after 50 min)
            tokenExpiry = System.currentTimeMillis() + (50 * 60 * 1000)
        }

        fun clearToken() {
            currentToken = null
            tokenExpiry = 0
        }
    }

    // Authentication interceptor to add Firebase ID token to requests
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            // Get cached token from TokenManager
            val idToken = TokenManager.getToken()
            if (idToken != null) {
                android.util.Log.d("AuthInterceptor", "Adding cached auth token to request")
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $idToken")
                    .build()
                return chain.proceed(request)
            } else {
                android.util.Log.w("AuthInterceptor", "No cached auth token available - proceeding without auth")
            }

            return chain.proceed(original)
        }
    }

    // Firebase Functions base URL
    private fun getBaseUrl(context: Context): String {
      // Use Firebase Functions URL for production
      return "https://api-y2udp4rn3q-uc.a.run.app/"
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

        // Create OkHttpClient with authentication interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
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
    
    // Function to refresh Firebase ID token and store it in TokenManager
    fun refreshFirebaseToken(onComplete: (String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            user.getIdToken(true)
                .addOnSuccessListener { result ->
                    val token = result.token
                    if (token != null) {
                        TokenManager.setToken(token)
                        android.util.Log.d("ApiClient", "Firebase ID token refreshed and cached")
                        onComplete(token)
                    } else {
                        android.util.Log.w("ApiClient", "Failed to get Firebase ID token")
                        onComplete(null)
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("ApiClient", "Error refreshing Firebase ID token", exception)
                    onComplete(null)
                }
        } else {
            android.util.Log.w("ApiClient", "No authenticated user")
            onComplete(null)
        }
    }
    
    // Function to clear cached token (call this on sign out)
    fun clearCachedToken() {
        TokenManager.clearToken()
        android.util.Log.d("ApiClient", "Cached Firebase ID token cleared")
    }
    
    // Legacy static instance (for backward compatibility)
    private var retrofit: Retrofit? = null
    
    val apiService: CloudInterviewApiService by lazy {
        // This will be initialized when first accessed
        // Note: You'll need to call createRetrofit() first
        throw IllegalStateException("Please call ApiClient.createRetrofit(context) first")
    }
} 