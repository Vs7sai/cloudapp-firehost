package com.v7techsolution.interviewfire

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.v7techsolution.interviewfire.api.ApiClient
import com.v7techsolution.interviewfire.api.models.Question
import com.v7techsolution.interviewfire.api.models.QuestionsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.LinearLayout

class QuestionActivity : AppCompatActivity() {
    
    private var currentQuestionIndex = 0
    private var questions: List<Question> = emptyList()
    private lateinit var questionText: TextView
    private lateinit var explanationText: TextView
    private lateinit var progressText: TextView
    private lateinit var nextButton: android.widget.ImageView
    private lateinit var prevButton: android.widget.ImageView
    private lateinit var difficultyText: TextView
    private lateinit var beginnerButton: android.widget.Button
    private lateinit var mediumButton: android.widget.Button
    private lateinit var proButton: android.widget.Button
    private lateinit var searchInput: android.widget.EditText
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    
    companion object {
        private const val TAG = "QuestionActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        
        // Hide the default action bar to remove any title
        supportActionBar?.hide()
        
        val topic = intent.getStringExtra("topic") ?: "AWS"
        val difficulty = intent.getStringExtra("difficulty") ?: "beginner"
        
        // Initialize views
        initializeViews()
        
        // Update the difficulty text
        difficultyText.text = when (difficulty.lowercase()) {
            "beginner" -> "B"
            "medium" -> "M"
            "pro" -> "P"
            else -> difficulty.capitalize()
        }
        
        // Update difficulty button states
        updateDifficultyButtonStates(difficulty)
        
        // Load questions from backend
        loadQuestions(topic, difficulty)
    }
    
    private fun initializeViews() {
        questionText = findViewById(R.id.question_text)
        explanationText = findViewById(R.id.explanation_text)
        progressText = findViewById(R.id.progress_text)
        nextButton = findViewById(R.id.btn_next)
        prevButton = findViewById(R.id.btn_previous)
        difficultyText = findViewById(R.id.difficulty_text)
        beginnerButton = findViewById(R.id.btn_beginner)
        mediumButton = findViewById(R.id.btn_medium)
        proButton = findViewById(R.id.btn_pro)
        searchInput = findViewById(R.id.search_question_input)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        
        // Set up navigation buttons
        nextButton.setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                displayCurrentQuestion()
            }
        }
        
        prevButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayCurrentQuestion()
            }
        }
        
        // Set up difficulty switching
        beginnerButton.setOnClickListener {
            switchDifficulty("beginner")
        }
        
        mediumButton.setOnClickListener {
            switchDifficulty("medium")
        }
        
        proButton.setOnClickListener {
            switchDifficulty("pro")
        }
        
        // Set up search functionality
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                performSearch()
                true
            } else {
                false
            }
        }
        
        // Add click listener for search input
        searchInput.setOnClickListener {
            // Show keyboard and focus on input immediately
            searchInput.requestFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
        
        // Add click listener for entire search container
        findViewById<LinearLayout>(R.id.search_container).setOnClickListener {
            // Show keyboard and focus on input when clicking anywhere on search container
            searchInput.requestFocus()
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
        
        // Also add focus change listener for better keyboard handling
        searchInput.onFocusChangeListener = android.view.View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Show keyboard when input gets focus
                val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }
        
        // Add text change listener for intelligent search
        searchInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val text = s?.toString()?.trim() ?: ""
                
                // Auto-search when user types 2 or more digits
                if (text.length >= 2 && text.all { it.isDigit() }) {
                    val questionNumber = text.toIntOrNull()
                    if (questionNumber != null && questionNumber > 0 && questionNumber <= questions.size) {
                        // Valid question number - auto-jump after a short delay
                        searchInput.postDelayed({
                            if (searchInput.text.toString().trim() == text) {
                                performSearch()
                            }
                        }, 500) // 500ms delay for better user experience
                    }
                }
            }
        })
        
        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            // Show refresh indicator
            swipeRefreshLayout.isRefreshing = true
            
            // Reload questions from backend
            val topic = intent.getStringExtra("topic") ?: "AWS"
            val difficulty = intent.getStringExtra("difficulty") ?: "beginner"
            loadQuestions(topic, difficulty)
        }
        
        // Set refresh colors
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        
        // Set up back button
        findViewById<android.widget.ImageView>(R.id.back_arrow).setOnClickListener {
            finish()
        }
    }
    
    private fun loadQuestions(topic: String, difficulty: String) {
        Log.d(TAG, "Loading questions for $topic - $difficulty")
        
        ApiClient.apiService.getQuestions(topic, difficulty).enqueue(object : Callback<QuestionsResponse> {
            override fun onResponse(call: Call<QuestionsResponse>, response: Response<QuestionsResponse>) {
                // Stop refresh indicator
                try {
                    swipeRefreshLayout?.isRefreshing = false
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping refresh", e)
                }
                
                if (response.isSuccessful && response.body() != null) {
                    val questionsResponse = response.body()!!
                    if (questionsResponse.success && questionsResponse.data.questions.isNotEmpty()) {
                        questions = questionsResponse.data.questions
                        Log.d(TAG, "Loaded ${questions.size} questions")
                        displayCurrentQuestion()
                    } else {
                        Log.e(TAG, "No questions found or API failed")
                        showError("No questions available for this topic and difficulty level.")
                    }
                } else {
                    Log.e(TAG, "API call unsuccessful: ${response.code()}")
                    showError("Failed to load questions. Please try again.")
                }
            }
            
            override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                // Stop refresh indicator
                try {
                    swipeRefreshLayout?.isRefreshing = false
                } catch (e: Exception) {
                    Log.e(TAG, "Error stopping refresh", e)
                }
                
                Log.e(TAG, "API call failed", t)
                showError("Network error. Please check your internet connection.")
            }
        })
    }
    
    private fun displayCurrentQuestion() {
        if (questions.isEmpty()) return
        
        val question = questions[currentQuestionIndex]
        
        // Update question text
        questionText.text = question.text
        
        // Update explanation text
        explanationText.text = question.explanation ?: "No explanation available"
        
        // Update progress
        progressText.text = "${currentQuestionIndex + 1} of ${questions.size}"
        
        // Update navigation button states
        prevButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.INVISIBLE
        nextButton.visibility = if (currentQuestionIndex < questions.size - 1) View.VISIBLE else View.INVISIBLE
    }
    
    private fun showError(message: String) {
        questionText.text = "Error"
        explanationText.text = message
        progressText.text = "0 of 0"
        nextButton.visibility = View.INVISIBLE
        prevButton.visibility = View.INVISIBLE
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun updateDifficultyButtonStates(selectedDifficulty: String) {
        // Reset all buttons to unselected state
        beginnerButton.background = getDrawable(R.drawable.button_glass_unselected)
        mediumButton.background = getDrawable(R.drawable.button_glass_unselected)
        proButton.background = getDrawable(R.drawable.button_glass_unselected)
        
        // Set selected button state
        when (selectedDifficulty.lowercase()) {
            "beginner" -> beginnerButton.background = getDrawable(R.drawable.button_glass_selected)
            "medium" -> mediumButton.background = getDrawable(R.drawable.button_glass_selected)
            "pro" -> proButton.background = getDrawable(R.drawable.button_glass_selected)
        }
    }
    
    private fun switchDifficulty(newDifficulty: String) {
        val topic = intent.getStringExtra("topic") ?: "AWS"
        
        // Update difficulty text
        difficultyText.text = when (newDifficulty.lowercase()) {
            "beginner" -> "B"
            "medium" -> "M"
            "pro" -> "P"
            else -> newDifficulty.capitalize()
        }
        
        // Update button states
        updateDifficultyButtonStates(newDifficulty)
        
        // Reset question index
        currentQuestionIndex = 0
        
        // Load new questions
        loadQuestions(topic, newDifficulty)
    }
    
    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    // Helper function to perform search
    private fun performSearch() {
        val searchText = searchInput.text.toString().trim()
        
        if (searchText.isEmpty()) {
            showSearchWarning("Please enter a question number")
            return
        }
        
        val questionNumber = searchText.toIntOrNull()
        if (questionNumber == null) {
            showSearchWarning("Please enter a valid number")
            return
        }
        
        if (questionNumber < 1) {
            showSearchWarning("Question number must be at least 1")
            return
        }
        
        if (questionNumber > questions.size) {
            showSearchWarning("Question number cannot exceed ${questions.size}")
            return
        }
        
        // Valid question number - jump to it
        val previousIndex = currentQuestionIndex
        currentQuestionIndex = questionNumber - 1
        displayCurrentQuestion()
        
        // Clear search input
        searchInput.text.clear()
        
        // Hide keyboard
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
        
        // Show success message
        val questionText = if (questionNumber == 1) "1st" else if (questionNumber == 2) "2nd" else if (questionNumber == 3) "3rd" else "${questionNumber}th"
        Toast.makeText(this, "Jumped to $questionText question", Toast.LENGTH_SHORT).show()
        
        // Log the jump for debugging
        Log.d(TAG, "Jumped from question ${previousIndex + 1} to question $questionNumber")
    }
    
    private fun showSearchWarning(message: String) {
        // Show warning message
        Toast.makeText(this, "⚠️ $message", Toast.LENGTH_SHORT).show()
        
        // Clear invalid input
        searchInput.text.clear()
        
        // Keep focus on search input for user to try again
        searchInput.requestFocus()
        
        // Show keyboard again
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
    }
}
