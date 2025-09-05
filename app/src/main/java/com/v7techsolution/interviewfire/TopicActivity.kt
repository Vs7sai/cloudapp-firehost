package com.v7techsolution.interviewfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import android.graphics.drawable.GradientDrawable
import com.v7techsolution.interviewfire.api.ApiClient
import com.v7techsolution.interviewfire.api.CloudInterviewApiService
import com.v7techsolution.interviewfire.api.models.DifficultiesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicActivity : AppCompatActivity() {
    
    private lateinit var difficultiesContainer: LinearLayout
    private var currentTopic: String = ""
    
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
            
            currentTopic = intent.getStringExtra("topic") ?: "AWS"
            Log.d(TAG, "Topic received: $currentTopic")
            
            // Initialize views
            initializeViews()
            
            // Update the toolbar title
            try {
                findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.title = currentTopic
            } catch (e: Exception) {
                Log.e(TAG, "Error setting toolbar title", e)
            }
            
            // Update the header topic text
            try {
                findViewById<TextView>(R.id.topic_title)?.text = currentTopic
            } catch (e: Exception) {
                Log.e(TAG, "Error setting topic title", e)
            }
            
            // Load dynamic difficulties for this topic
            loadDifficulties()
            
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
            difficultiesContainer = findViewById(R.id.difficulties_container)
            Log.d(TAG, "Views initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            throw e
        }
    }
    
    private fun loadDifficulties() {
        Log.d(TAG, "Loading difficulties for topic: $currentTopic")
        showLoadingState()
        
        val retrofit = ApiClient.createRetrofit(this)
        val apiService = retrofit.create(CloudInterviewApiService::class.java)
        val call = apiService.getDifficulties()
        
        call.enqueue(object : Callback<DifficultiesResponse> {
            override fun onResponse(call: Call<DifficultiesResponse>, response: Response<DifficultiesResponse>) {
                try {
                    if (response.isSuccessful && response.body() != null) {
                        val difficultiesResponse = response.body()!!
                        if (difficultiesResponse.success) {
                            val topicDifficulties = difficultiesResponse.data[currentTopic.lowercase()]
                            if (topicDifficulties != null && topicDifficulties.isNotEmpty()) {
                                Log.d(TAG, "Found ${topicDifficulties.size} difficulties for $currentTopic: $topicDifficulties")
                                displayDifficulties(topicDifficulties)
                            } else {
                                Log.w(TAG, "No difficulties found for topic: $currentTopic, using defaults")
                                displayDifficulties(listOf("beginner", "medium", "pro")) // fallback
                            }
                        } else {
                            Log.e(TAG, "API returned success=false")
                            showError("Failed to load difficulties")
                        }
                    } else {
                        Log.e(TAG, "Failed to load difficulties: ${response.code()}")
                        showError("Failed to load difficulties")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing difficulties response", e)
                    showError("Error loading difficulties: ${e.message}")
                }
            }
            
            override fun onFailure(call: Call<DifficultiesResponse>, t: Throwable) {
                Log.e(TAG, "Network error loading difficulties", t)
                showError("Network error: ${t.message}")
            }
        })
    }
    
    private fun displayDifficulties(difficulties: List<String>) {
        runOnUiThread {
            try {
                Log.d(TAG, "Displaying ${difficulties.size} difficulties: $difficulties")
                difficultiesContainer.removeAllViews()
                
                for (difficulty in difficulties) {
                    val difficultyCard = createDifficultyCard(difficulty)
                    difficultiesContainer.addView(difficultyCard)
                }
                
                hideLoadingState()
                Log.d(TAG, "Successfully displayed all difficulty cards")
            } catch (e: Exception) {
                Log.e(TAG, "Error displaying difficulties", e)
                showError("Error displaying difficulties: ${e.message}")
            }
        }
    }
    
    private fun createDifficultyCard(difficulty: String): LinearLayout {
        val context = this
        
        // Get color based on difficulty
        val backgroundColor = when(difficulty.lowercase()) {
            "beginner" -> ContextCompat.getColor(context, R.color.beginner_color)
            "medium" -> ContextCompat.getColor(context, R.color.medium_color) 
            "pro", "advanced" -> ContextCompat.getColor(context, R.color.pro_color)
            "commands" -> ContextCompat.getColor(context, R.color.commands_color)
            "example" -> ContextCompat.getColor(context, R.color.example_color)
            else -> ContextCompat.getColor(context, R.color.default_difficulty_color)
        }
        
        // Create main LinearLayout with colored background and rounded corners
        val cardLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.difficulty_card_height)
            ).apply {
                setMargins(0, 0, 0, resources.getDimensionPixelSize(R.dimen.difficulty_card_margin))
            }
            orientation = LinearLayout.HORIZONTAL
            background = createRoundedBackground(backgroundColor)
            setPadding(
                resources.getDimensionPixelSize(R.dimen.difficulty_card_padding),
                resources.getDimensionPixelSize(R.dimen.difficulty_card_padding),
                resources.getDimensionPixelSize(R.dimen.difficulty_card_padding),
                resources.getDimensionPixelSize(R.dimen.difficulty_card_padding)
            )
            gravity = android.view.Gravity.CENTER_VERTICAL
            elevation = 8f
            isClickable = true
            isFocusable = true
            foreground = ContextCompat.getDrawable(context, R.drawable.ripple_effect)
        }
        
        // Create inner LinearLayout for text content
        val innerLayout = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            orientation = LinearLayout.VERTICAL
        }
        
        // Create title TextView
        val titleTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = difficulty.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            textSize = 28f
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 4)
        }
        
        // Create description TextView
        val descriptionTextView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = when(difficulty.lowercase()) {
                "beginner" -> "Start with basic concepts"
                "medium" -> "Intermediate level questions"
                "pro", "advanced" -> "Advanced expert questions"
                "commands" -> "Command line and practical"
                "example" -> "Examples and use cases"
                else -> "Practice questions for $difficulty"
            }
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.white))
            alpha = 0.9f
        }
        
        // Add text views to inner layout
        innerLayout.addView(titleTextView)
        innerLayout.addView(descriptionTextView)
        
        // Add inner layout to main card with weight
        cardLayout.addView(innerLayout)
        
        // Set click listener
        cardLayout.setOnClickListener {
            Log.d(TAG, "$difficulty difficulty selected")
            startQuestionActivity(currentTopic, difficulty)
        }
        
        return cardLayout
    }
    
    private fun showLoadingState() {
        runOnUiThread {
            difficultiesContainer.removeAllViews()
            val loadingText = TextView(this).apply {
                text = "Loading difficulties..."
                textSize = 16f
                setPadding(32, 32, 32, 32)
                setTextColor(ContextCompat.getColor(this@TopicActivity, R.color.black))
            }
            difficultiesContainer.addView(loadingText)
        }
    }
    
    private fun hideLoadingState() {
        // Loading state is automatically hidden when we display difficulties
    }
    
    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            Log.e(TAG, "Error: $message")
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
    
    private fun createRoundedBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = resources.getDimensionPixelSize(R.dimen.difficulty_card_corner_radius).toFloat()
        }
    }
}