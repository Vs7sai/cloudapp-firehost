package com.v7techsolution.interviewfire

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

object AuthHelper {
    private const val TAG = "AuthHelper"
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    // Check if user is authenticated
    fun isUserAuthenticated(): Boolean {
        val currentUser = firebaseAuth.currentUser
        val isAuthenticated = currentUser != null
        Log.d(TAG, "User authentication check: $isAuthenticated")
        return isAuthenticated
    }
    
    // Get current user
    fun getCurrentUser() = firebaseAuth.currentUser
    
    // Sign out user
    fun signOut() {
        try {
            firebaseAuth.signOut()
            Log.d(TAG, "User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out", e)
        }
    }
    
    // Get Firebase ID token
    fun getFirebaseIdToken(callback: (String?) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result?.token
                        Log.d(TAG, "Firebase ID token retrieved successfully")
                        callback(token)
                    } else {
                        Log.e(TAG, "Failed to get Firebase ID token", task.exception)
                        callback(null)
                    }
                }
        } else {
            Log.w(TAG, "No authenticated user found")
            callback(null)
        }
    }
    
    // Simple email/password authentication
    fun signInWithEmail(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sign-in successful")
                    callback(true, null)
                } else {
                    Log.e(TAG, "Email sign-in failed", task.exception)
                    callback(false, task.exception?.message)
                }
            }
    }
    
    // Create account with email/password
    fun createAccountWithEmail(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Account creation successful")
                    callback(true, null)
                } else {
                    Log.e(TAG, "Account creation failed", task.exception)
                    callback(false, task.exception?.message)
                }
            }
    }
}
