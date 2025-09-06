package com.v7techsolution.interviewfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TopicActivity : AppCompatActivity() {
    
    private lateinit var beginnerCard: LinearLayout
    private lateinit var mediumCard: LinearLayout
    private lateinit var proCard: LinearLayout
    
    companion object {
        private const val TAG = "TopicActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check authentication first
        if (!AuthHelper.isUserAuthenticated()) {
            Log.d(TAG, "User not authenticated, redirecting to LoginActivity")
            redirectToLogin()
            return
        }

        try {
            Log.d(TAG, "TopicActivity onCreate started")
            setContentView(R.layout.activity_topic)
            
            // Hide the default action bar to remove any title
            supportActionBar?.hide()
            
            val topic = intent.getStringExtra("topic") ?: "AWS"
            Log.d(TAG, "Topic received: $topic")
            
            // Initialize views
            initializeViews()
            
            // Update the toolbar title
            try {
                findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.title = topic
            } catch (e: Exception) {
                Log.e(TAG, "Error setting toolbar title", e)
            }
            
            // Update the header topic text
            try {
                findViewById<TextView>(R.id.topic_title)?.text = topic
            } catch (e: Exception) {
                Log.e(TAG, "Error setting topic title", e)
            }
            
            // Set up click listeners for difficulty cards
            beginnerCard.setOnClickListener {
                Log.d(TAG, "Beginner difficulty selected")
                startQuestionActivity(topic, "beginner")
            }
            
            mediumCard.setOnClickListener {
                Log.d(TAG, "Medium difficulty selected")
                startQuestionActivity(topic, "medium")
            }
            
            proCard.setOnClickListener {
                Log.d(TAG, "Pro difficulty selected")
                startQuestionActivity(topic, "pro")
            }
            
            // Back button
            try {
                findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.setNavigationOnClickListener {
                    Log.d(TAG, "Back button clicked")
                    finish()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up back button", e)
            }
            
            Log.d(TAG, "TopicActivity onCreate completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in TopicActivity onCreate", e)
            Toast.makeText(this, "Error initializing topic view: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun initializeViews() {
        try {
            Log.d(TAG, "Initializing views")
            beginnerCard = findViewById(R.id.card_beginner)
            mediumCard = findViewById(R.id.card_medium)
            proCard = findViewById(R.id.card_pro)
            Log.d(TAG, "Views initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            throw e
        }
    }
    
    private fun startQuestionActivity(topic: String, difficulty: String) {
        try {
            Log.d(TAG, "Starting QuestionActivity for topic: $topic, difficulty: $difficulty")
            val intent = Intent(this, QuestionActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("difficulty", difficulty)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting QuestionActivity", e)
            Toast.makeText(this, "Error opening questions: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun redirectToLogin() {
        Log.d(TAG, "Redirecting to LoginActivity")
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}