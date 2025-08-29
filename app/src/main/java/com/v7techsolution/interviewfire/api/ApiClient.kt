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

    // Authentication interceptor to add Firebase ID token to requests
    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            // Get Firebase ID token asynchronously
            val idToken = getFirebaseIdToken()
            if (idToken != null) {
                android.util.Log.d("AuthInterceptor", "Adding auth token to request")
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $idToken")
                    .build()
                return chain.proceed(request)
            } else {
                android.util.Log.w("AuthInterceptor", "No auth token available - proceeding without auth")
            }

            return chain.proceed(original)
        }

        private fun getFirebaseIdToken(): String? {
            return try {
                val user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    android.util.Log.w("AuthInterceptor", "No authenticated user")
                    return null
                }

                // For development/testing, create a mock token if user exists
                // This simulates a Firebase ID token format
                val mockToken = createMockFirebaseToken(user.uid)
                android.util.Log.d("AuthInterceptor", "Using mock Firebase ID token for user: ${user.email}")
                mockToken

            } catch (e: Exception) {
                android.util.Log.e("AuthInterceptor", "Error getting Firebase ID token", e)
                null
            }
        }

        private fun createMockFirebaseToken(uid: String): String {
            // Create a mock Firebase ID token format for development
            // This mimics the structure of a real Firebase ID token
            val header = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9" // Mock header
            val payload = android.util.Base64.encodeToString(
                "{\"iss\":\"https://securetoken.google.com/mock-project\",\"aud\":\"mock-project\",\"auth_time\":${System.currentTimeMillis()/1000},\"user_id\":\"$uid\",\"sub\":\"$uid\",\"iat\":${System.currentTimeMillis()/1000},\"exp\":${(System.currentTimeMillis()/1000) + 3600},\"email\":\"user@example.com\",\"email_verified\":true}".toByteArray(),
                android.util.Base64.NO_WRAP
            )
            val signature = "mock_signature_for_development"
            return "$header.$payload.$signature"
        }
    }

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
    
    // Create Retrofit instance with dynamic base URL and authentication
    fun createRetrofit(context: Context): Retrofit {
        val baseUrl = getBaseUrl(context)
        android.util.Log.d("ApiClient", "Using base URL: $baseUrl")

        // Create OkHttpClient with authentication interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
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