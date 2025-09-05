package com.v7techsolution.interviewfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.v7techsolution.interviewfire.api.ApiClient
import com.v7techsolution.interviewfire.api.models.Question
import com.v7techsolution.interviewfire.api.models.QuestionsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.LinearLayout
import android.widget.ScrollView
import com.v7techsolution.interviewfire.api.CloudInterviewApiService

class QuestionActivity : AppCompatActivity() {
    
    private var currentQuestionIndex = 0
    private var questions: List<Question> = emptyList()
    private lateinit var adMobHelper: AdMobHelper
    private var questionsAnswered = 0
    private lateinit var questionText: AppCompatTextView
    private lateinit var explanationText: MarkdownTextView
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

        // Check authentication first
        if (!AuthHelper.isUserAuthenticated()) {
            Log.d(TAG, "User not authenticated, redirecting to LoginActivity")
            redirectToLogin()
            return
        }

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

        // Initialize AdMob
        adMobHelper = AdMobHelper(this)
        adMobHelper.initialize()
        
        // Set up navigation buttons
        nextButton.setOnClickListener {
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                questionsAnswered++
                
                // Show ad every 12th question
                if (questionsAnswered % 12 == 0) {
                    Log.d(TAG, "Showing ad after $questionsAnswered questions")
                    adMobHelper.showInterstitialAd(this) {
                        // Continue to next question after ad is closed
                        displayCurrentQuestion()
                    }
                    // Preload next ad
                    adMobHelper.preloadAd()
                } else {
                    displayCurrentQuestion()
                }
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
        
        // Set up search functionality with robust error handling
        setupSearchFunctionality()
        
        // Set up pull-to-refresh with smart scroll detection
        setupSmartRefreshControl()
        
        // Set refresh colors
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        
        // Configure refresh behavior to be less sensitive
        swipeRefreshLayout.setDistanceToTriggerSync(200) // Increase distance required to trigger refresh
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT) // Use default size
        
        // Set up scroll detection for answer section
        setupAnswerScrollDetection()
        
        // Configure SwipeRefreshLayout to not interfere with answer scrolling
        swipeRefreshLayout.setNestedScrollingEnabled(false)
        
        // Additional scroll-based refresh prevention
        preventScrollBasedRefresh()
        
        // Show refresh guidance to user
        showRefreshGuidance()
        
        // Set up dynamic bottom navigation handling
        setupDynamicBottomNavigation()
    }
    
    private fun setupDynamicBottomNavigation() {
        // Handle system window insets for dynamic bottom navigation
        val rootView = findViewById<android.view.View>(android.R.id.content)
        rootView.setOnApplyWindowInsetsListener { _, insets ->
            val bottomInset = insets.systemWindowInsets.bottom
            val bottomNavigation = findViewById<LinearLayout>(R.id.bottom_navigation)
            
            // Add bottom padding to account for system navigation bar
            if (bottomInset > 0) {
                bottomNavigation.setPadding(
                    bottomNavigation.paddingLeft,
                    bottomNavigation.paddingTop,
                    bottomNavigation.paddingRight,
                    bottomInset + 16 // 16dp base padding + system inset
                )
                Log.d(TAG, "System navigation bar detected, added ${bottomInset}dp bottom padding")
            } else {
                // Reset to default padding if no system navigation bar
                bottomNavigation.setPadding(
                    bottomNavigation.paddingLeft,
                    bottomNavigation.paddingTop,
                    bottomNavigation.paddingRight,
                    16
                )
                Log.d(TAG, "No system navigation bar, using default padding")
            }
            
            insets
        }
    }
    
    private fun showRefreshGuidance() {
        // Show user guidance about refresh functionality
        try {
            // You can show a tooltip or help text here
            Log.d(TAG, "Refresh guidance: Pull down from top to refresh questions")
            
            // Optional: Show a one-time help message
            // This could be a Snackbar or Toast for first-time users
        } catch (e: Exception) {
            Log.e(TAG, "Error showing refresh guidance", e)
        }
    }
    
    private fun provideRefreshFeedback() {
        // Provide comprehensive feedback about refresh state
        val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
        val isAtTop = answerScrollView.scrollY == 0
        
        if (isAtTop) {
            // Show positive feedback that refresh is available
            Log.d(TAG, "Refresh feedback: Available at top")
        } else {
            // Show guidance to user
            Log.d(TAG, "Refresh feedback: Scroll to top first")
            // You can add visual indicators here if needed
        }
    }
    
    private fun handleLongAnswerRefresh() {
        // Handle refresh behavior for long answers
        val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
        val contentHeight = answerScrollView.getChildAt(0).height
        val viewportHeight = answerScrollView.height
        val isLongAnswer = contentHeight > viewportHeight * 2 // More than 2x viewport height
        
        if (isLongAnswer) {
            // For long answers, be more strict about refresh
            swipeRefreshLayout.setDistanceToTriggerSync(300) // Increase distance
            Log.d(TAG, "Long answer detected - increased refresh threshold")
        } else {
            // For short answers, use normal threshold
            swipeRefreshLayout.setDistanceToTriggerSync(200)
            Log.d(TAG, "Short answer - normal refresh threshold")
        }
    }
    
    private fun setupSmartRefreshControl() {
        val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
        var isRefreshing = false
        
        // Only allow refresh when at the very top of the content
        swipeRefreshLayout.setOnRefreshListener {
            // Prevent multiple refresh calls
            if (isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            }
            
            // Check if we're at the top of the answer scroll view
            if (answerScrollView.scrollY == 0) {
                isRefreshing = true
                // Show refresh indicator
                swipeRefreshLayout.isRefreshing = true
                
                // Reload questions from backend
                val topic = intent.getStringExtra("topic") ?: "AWS"
                val difficulty = intent.getStringExtra("difficulty") ?: "beginner"
                loadQuestions(topic, difficulty)
            } else {
                // Handle accidental refresh
                handleAccidentalRefresh()
            }
        }
        
        // Custom refresh trigger logic
        var isRefreshEnabled = true
        answerScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // Enable refresh only when at the very top
            isRefreshEnabled = scrollY == 0
            swipeRefreshLayout.isEnabled = isRefreshEnabled
            
            // Provide visual feedback about refresh availability
            if (isRefreshEnabled) {
                Log.d(TAG, "Refresh enabled - at top of content")
                // Show subtle visual indicator that refresh is available
                showRefreshAvailableIndicator()
            } else {
                Log.d(TAG, "Refresh disabled - scrolling in answer section")
                // Hide refresh indicator
                hideRefreshAvailableIndicator()
            }
        }
        
        // Set up child scroll up callback for better refresh control
        swipeRefreshLayout.setOnChildScrollUpCallback { _, _ ->
            // Only allow refresh when the answer scroll view is at the top
            answerScrollView.scrollY == 0
        }
        
        // Initially enable refresh
        swipeRefreshLayout.isEnabled = true
    }
    
    private fun resetRefreshState() {
        try {
            swipeRefreshLayout.isRefreshing = false
            // Re-enable refresh when at top
            val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
            swipeRefreshLayout.isEnabled = answerScrollView.scrollY == 0
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting refresh state", e)
        }
    }
    
    private fun handleAccidentalRefresh() {
        // If refresh is triggered while not at top, show user feedback
        val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
        if (answerScrollView.scrollY > 0) {
            // Provide haptic feedback
            try {
                val vibrator = getSystemService(android.content.Context.VIBRATOR_SERVICE) as android.os.Vibrator
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val effect = android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error providing haptic feedback", e)
            }
            
            Toast.makeText(this, "Scroll to top to refresh", Toast.LENGTH_SHORT).show()
            resetRefreshState()
        }
    }
    
    private fun handleRefreshEdgeCases() {
        // Handle cases where refresh might get stuck
        swipeRefreshLayout.setOnRefreshListener {
            try {
                // Check if we're at the top of the answer scroll view
                val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
                if (answerScrollView.scrollY == 0) {
                    // Show refresh indicator
                    swipeRefreshLayout.isRefreshing = true
                    
                    // Reload questions from backend
                    val topic = intent.getStringExtra("topic") ?: "AWS"
                    val difficulty = intent.getStringExtra("difficulty") ?: "beginner"
                    loadQuestions(topic, difficulty)
                } else {
                    // Cancel refresh if not at top
                    swipeRefreshLayout.isRefreshing = false
                    Log.d(TAG, "Refresh cancelled - not at top of content")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error during refresh", e)
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(this, "Refresh failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupSearchFunctionality() {
        try {
            Log.d(TAG, "Setting up search functionality...")
            
            // 1. Set up search input click listener with mobile support
            searchInput.setOnClickListener {
                Log.d(TAG, "Search input clicked")
                activateSearchInput()
            }
            
            // Enhanced touch listener for the white input area
            searchInput.setOnTouchListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        Log.d(TAG, "Search input touch down - showing cursor")
                        // Show cursor immediately on touch down
                        searchInput.requestFocus()
                        searchInput.setSelection(searchInput.text.length)
                        true
                    }
                    android.view.MotionEvent.ACTION_UP -> {
                        Log.d(TAG, "Search input touch up - activating input")
                        activateSearchInput()
                        true
                    }
                    android.view.MotionEvent.ACTION_MOVE -> {
                        // Keep cursor visible while touching
                        if (searchInput.hasFocus()) {
                            searchInput.setSelection(searchInput.text.length)
                        }
                        true
                    }
                    else -> false
                }
            }
            
            // 2. Set up search container click listener with mobile support
            try {
                val searchContainer = findViewById<LinearLayout>(R.id.search_container)
                if (searchContainer != null) {
                    // Click listener
                    searchContainer.setOnClickListener {
                        Log.d(TAG, "Search container clicked")
                        activateSearchInput()
                    }
                    
                    // Enhanced touch listener for mobile
                    searchContainer.setOnTouchListener { _, event ->
                        when (event.action) {
                            android.view.MotionEvent.ACTION_DOWN -> {
                                Log.d(TAG, "Search container touch down")
                                // Show cursor immediately on touch down
                                searchInput.requestFocus()
                                searchInput.setSelection(searchInput.text.length)
                                true
                            }
                            android.view.MotionEvent.ACTION_UP -> {
                                Log.d(TAG, "Search container touch up")
                                activateSearchInput()
                                true
                            }
                            android.view.MotionEvent.ACTION_MOVE -> {
                                // Keep cursor visible while touching
                                if (searchInput.hasFocus()) {
                                    searchInput.setSelection(searchInput.text.length)
                                }
                                true
                            }
                            else -> false
                        }
                    }
                    
                    Log.d(TAG, "Search container touch and click listeners set successfully")
                } else {
                    Log.e(TAG, "Search container not found!")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up search container listeners", e)
            }
            
            // 3. Set up editor action listener
            searchInput.setOnEditorActionListener { _, actionId, _ ->
                Log.d(TAG, "Editor action: $actionId")
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "Editor action DONE - performing search")
                    performSearch()
                true
            } else {
                false
            }
        }
        
            // 4. Set up focus change listener
            searchInput.onFocusChangeListener = android.view.View.OnFocusChangeListener { _, hasFocus ->
                Log.d(TAG, "Search input focus changed: $hasFocus")
                if (hasFocus) {
                    try {
                        // Ensure cursor is visible when focus is gained
                        searchInput.setSelection(searchInput.text.length)
                        
                        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                        imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
                        
                        // Keep cursor visible for 15 seconds
                        searchInput.postDelayed({
                            if (searchInput.hasFocus()) {
                                searchInput.setSelection(searchInput.text.length) // Ensure cursor is still visible
                            }
                        }, 15000) // 15 seconds
                        
                        Log.d(TAG, "Keyboard shown on focus, cursor will stay visible for 15 seconds")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error showing keyboard on focus", e)
                    }
                }
            }
            
            // 5. Set up text change listener for auto-search
            searchInput.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    val text = s?.toString()?.trim() ?: ""
                    Log.d(TAG, "Search text changed: '$text'")
                    
                    // Keep cursor visible after text changes
                    searchInput.postDelayed({
                        if (searchInput.hasFocus()) {
                            searchInput.setSelection(searchInput.text.length)
                        }
                    }, 100) // Small delay to ensure cursor is visible
                    
                    // Auto-search when user types 2 or more digits
                    if (text.length >= 2 && text.all { it.isDigit() }) {
                        val questionNumber = text.toIntOrNull()
                        if (questionNumber != null && questionNumber > 0 && questionNumber <= questions.size) {
                            Log.d(TAG, "Auto-search triggered for question $questionNumber")
                            // Valid question number - auto-jump after a short delay
                            searchInput.postDelayed({
                                if (searchInput.text.toString().trim() == text) {
                                    Log.d(TAG, "Executing auto-search")
                                    performSearch()
                                }
                            }, 500) // 500ms delay for better user experience
                        }
                    }
                }
            })
            
            Log.d(TAG, "Search functionality setup completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error setting up search functionality", e)
            Toast.makeText(this, "Search setup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun activateSearchInput() {
        try {
            Log.d(TAG, "Activating search input...")
            
            // Focus and show cursor for at least 15 seconds
            searchInput.requestFocus()
            searchInput.setSelection(searchInput.text.length) // Move cursor to end
            
            // Ensure cursor is blinking and visible
            searchInput.postDelayed({
                if (searchInput.hasFocus()) {
                    searchInput.setSelection(searchInput.text.length)
                }
            }, 100) // Small delay to ensure cursor is visible
            
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(searchInput, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            
            // Keep cursor visible for 15 seconds
            searchInput.postDelayed({
                if (searchInput.hasFocus()) {
                    searchInput.setSelection(searchInput.text.length) // Ensure cursor is still visible
                }
            }, 15000) // 15 seconds
            
            Log.d(TAG, "Search input activated, cursor will stay visible for 15 seconds")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error activating search input", e)
        }
    }
    
    private fun setupAnswerScrollDetection() {
        val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
        val scrollHint = findViewById<TextView>(R.id.scroll_hint)
        
        // Show scroll hint if content is scrollable
        answerScrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val isScrollable = answerScrollView.getChildAt(0).height > answerScrollView.height
            scrollHint.visibility = if (isScrollable) View.VISIBLE else View.GONE
            
            if (isScrollable) {
                Log.d(TAG, "Answer content is scrollable - showing scroll hint")
            }
        }
        
        // Detect scroll events to provide feedback and manage refresh state
        answerScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            // Update refresh state based on scroll position
            swipeRefreshLayout.isEnabled = scrollY == 0
            
            if (scrollY > oldScrollY) {
                // Scrolling down - hide hint after a delay
                scrollHint.postDelayed({
                    if (scrollY > 100) { // Hide hint after scrolling down a bit
                        scrollHint.animate()
                            .alpha(0f)
                            .setDuration(500)
                            .withEndAction {
                                scrollHint.visibility = View.GONE
                            }
                            .start()
                    }
                }, 1000)
            } else if (scrollY == 0) {
                // At the top - show hint again and enable refresh
                scrollHint.visibility = View.VISIBLE
                scrollHint.alpha = 1f
                swipeRefreshLayout.isEnabled = true
            }
        }
    }
    
    private fun loadQuestions(topic: String, difficulty: String) {
        Log.d(TAG, "Loading questions for $topic - $difficulty")
        
        try {
            Log.d(TAG, "Making API call to getQuestions()...")
            
            // Create API client with dynamic base URL
            val retrofit = ApiClient.createRetrofit(this)
            val apiService = retrofit.create(CloudInterviewApiService::class.java)
            
            apiService.getQuestions(topic, difficulty).enqueue(object : Callback<QuestionsResponse> {
            override fun onResponse(call: Call<QuestionsResponse>, response: Response<QuestionsResponse>) {
                    // Stop refresh indicator
                    try {
                        resetRefreshState()
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
                        resetRefreshState()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping refresh", e)
                    }
                    
                Log.e(TAG, "API call failed", t)
                showError("Network error. Please check your internet connection.")
            }
        })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading questions", e)
            showError("Error: ${e.message}")
        }
    }
    
    private fun displayCurrentQuestion() {
        if (questions.isEmpty()) return

        val question = questions[currentQuestionIndex]

        // Handle refresh behavior based on answer length
        handleLongAnswerRefresh()

        // Update question text
        questionText.text = question.text

        // Update explanation text with Markdown support
        explanationText.setMarkdownText(question.explanation ?: "No explanation available")

        // Update progress
        progressText.text = "${currentQuestionIndex + 1} of ${questions.size}"

        // Update navigation button states
        prevButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.INVISIBLE
        nextButton.visibility = if (currentQuestionIndex < questions.size - 1) View.VISIBLE else View.INVISIBLE

        // Display code examples and commands based on question content
        displayCodeExamples(question)
    }

    private fun displayCodeExamples(question: Question) {
        // Remove hardcoded examples - only use backend data with syntax highlighting
        // The MarkdownTextView will automatically handle code blocks from backend data
        // No additional UI components needed since syntax highlighting is handled by MarkdownTextView
    }




    
    private fun showError(message: String) {
        questionText.text = "Error"
        explanationText.setMarkdownText(message)
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
        
        // Show success message with proper ordinal numbers
        val questionText = when (questionNumber) {
            1 -> "1st"
            2 -> "2nd"
            3 -> "3rd"
            21 -> "21st"
            22 -> "22nd"
            23 -> "23rd"
            31 -> "31st"
            32 -> "32nd"
            33 -> "33rd"
            41 -> "41st"
            42 -> "42nd"
            43 -> "43rd"
            51 -> "51st"
            52 -> "52nd"
            53 -> "53rd"
            61 -> "61st"
            62 -> "62nd"
            63 -> "63rd"
            71 -> "71st"
            72 -> "72nd"
            73 -> "73rd"
            81 -> "81st"
            82 -> "82nd"
            83 -> "83rd"
            91 -> "91st"
            92 -> "92nd"
            93 -> "93rd"
            else -> "${questionNumber}th"
        }
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

    private fun showRefreshAvailableIndicator() {
        // Show subtle visual feedback that refresh is available
        try {
            // You can add a subtle animation or color change here
            // For now, we'll just log it
            Log.d(TAG, "Refresh indicator: Available")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing refresh indicator", e)
        }
    }

    private fun hideRefreshAvailableIndicator() {
        // Hide refresh availability indicator
        try {
            // You can add a subtle animation or color change here
            // For now, we'll just log it
            Log.d(TAG, "Refresh indicator: Not available")
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding refresh indicator", e)
        }
    }
    
    private fun preventScrollBasedRefresh() {
        // Additional safety to prevent refresh during active scrolling
        val answerScrollView = findViewById<ScrollView>(R.id.answer_scroll_view)
        
        // Disable refresh if user is actively scrolling
        answerScrollView.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_MOVE -> {
                    // Disable refresh during scroll
                    swipeRefreshLayout.isEnabled = false
                    false // Don't consume the event
                }
                android.view.MotionEvent.ACTION_UP -> {
                    // Re-enable refresh only if at top
                    swipeRefreshLayout.isEnabled = answerScrollView.scrollY == 0
                    false // Don't consume the event
                }
                else -> false
            }
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
