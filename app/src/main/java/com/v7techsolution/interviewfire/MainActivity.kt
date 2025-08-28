package com.v7techsolution.interviewfire

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.v7techsolution.interviewfire.api.ApiClient
import com.v7techsolution.interviewfire.api.models.Topic
import com.v7techsolution.interviewfire.api.models.TopicsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var topicsContainer: LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var connectivityManager: ConnectivityManager
    private var isNetworkAvailable = false
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "MainActivity onCreate started")
        
        try {
            // Hide the default action bar to remove any title
            supportActionBar?.hide()
            
            setContentView(R.layout.activity_main)
            
            // Initialize views
            initializeViews()
            
            // Setup pull-to-refresh
            setupSwipeRefresh()
            
            // Setup network monitoring
            setupNetworkCallback()
            
            // Check internet connectivity and load topics
            checkInternetConnectivity()
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in MainActivity onCreate", e)
            Toast.makeText(this, "App initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun initializeViews() {
        Log.d(TAG, "Initializing views...")
        
        try {
            // Main views
            topicsContainer = findViewById(R.id.topics_container)
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
            
            // Initialize connectivity manager
            connectivityManager = ContextCompat.getSystemService(this, ConnectivityManager::class.java)!!
            
            Log.d(TAG, "Views initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            Toast.makeText(this, "Error initializing views: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun checkInternetConnectivity() {
        try {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            
            isNetworkAvailable = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            
            if (isNetworkAvailable) {
                Log.d(TAG, "Internet connection available")
                // Load topics if we have internet
                loadTopicsFromBackend()
            } else {
                Log.w(TAG, "No internet connection")
                showNetworkError("No internet connection detected. Please check your WiFi or mobile data.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking internet connectivity", e)
            showNetworkError("Error checking network connection. Please try again.")
        }
    }
    
    private fun setupNetworkCallback() {
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            
            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d(TAG, "Network became available")
                    isNetworkAvailable = true
                    runOnUiThread {
                        // Reload topics when network becomes available
                        loadTopicsFromBackend()
                    }
                }
                
                override fun onLost(network: Network) {
                    Log.w(TAG, "Network lost")
                    isNetworkAvailable = false
                    runOnUiThread {
                        showNetworkError("Network connection lost. Please check your WiFi or mobile data connection.")
                    }
                }
            }
            
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up network callback", e)
        }
    }
    
    private fun setupSwipeRefresh() {
        try {
            swipeRefreshLayout.setOnRefreshListener {
                // Show refresh indicator
                swipeRefreshLayout.isRefreshing = true
                
                // Check internet connectivity before refreshing
                if (isNetworkAvailable) {
                    // Reload topics from backend
                    loadTopicsFromBackend()
                } else {
                    // Stop refresh and show error
                    swipeRefreshLayout.isRefreshing = false
                    showNetworkError("No internet connection detected. Please check your WiFi or mobile data.")
                }
            }
            
            // Set refresh colors
            swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up swipe refresh", e)
        }
    }
    
    private fun loadTopicsFromBackend() {
        Log.d(TAG, "Loading topics from backend...")
        
        try {
            ApiClient.apiService.getTopics().enqueue(object : Callback<TopicsResponse> {
                override fun onResponse(call: Call<TopicsResponse>, response: Response<TopicsResponse>) {
                    // Stop refresh indicator
                    try {
                        swipeRefreshLayout?.isRefreshing = false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping refresh", e)
                    }
                    
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val topicsResponse = response.body()!!
                            if (topicsResponse.success && topicsResponse.data.isNotEmpty()) {
                                Log.d(TAG, "Topics loaded: ${topicsResponse.data.size}")
                                createTopicCards(topicsResponse.data)
                            } else {
                                Log.e(TAG, "Topics API failed or empty data")
                                showBackendError("Backend server returned empty data. Please try again later.")
                            }
                        } else {
                            Log.e(TAG, "Topics API call unsuccessful: ${response.code()}")
                            when (response.code()) {
                                404 -> showBackendError("Backend server not found. Please check if the server is running.")
                                500 -> showBackendError("Backend server error. Please try again later.")
                                503 -> showBackendError("Backend server is temporarily unavailable. Please try again later.")
                                else -> showBackendError("Backend server error (${response.code()}). Please try again later.")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing topics response", e)
                        showBackendError("Error processing server response. Please try again.")
                    }
                }
                
                override fun onFailure(call: Call<TopicsResponse>, t: Throwable) {
                    // Stop refresh indicator
                    try {
                        swipeRefreshLayout?.isRefreshing = false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping refresh", e)
                    }
                    
                    Log.e(TAG, "Topics API call failed", t)
                    
                    // Check what type of network error occurred
                    when {
                        t.message?.contains("timeout", ignoreCase = true) == true -> 
                            showBackendError("Backend server is taking too long to respond. Please try again.")
                        t.message?.contains("unable to resolve host", ignoreCase = true) == true -> 
                            showBackendError("Cannot connect to backend server. Please check your internet connection.")
                        t.message?.contains("failed to connect", ignoreCase = true) == true -> 
                            showBackendError("Backend server is not reachable. Please check if the server is running.")
                        else -> 
                            showBackendError("Network error: ${t.message}. Please check your connection and try again.")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading topics", e)
            showBackendError("Error: ${e.message}")
        }
    }
    
    private fun createTopicCards(topics: List<Topic>) {
        try {
            if (topics.isEmpty()) {
                Log.w(TAG, "No topics received")
                showError("No topics available")
                return
            }
            
            if (topicsContainer == null) {
                Log.e(TAG, "topicsContainer is null")
                return
            }
            
            Log.d(TAG, "Creating ${topics.size} topic cards")
            
            // Clear existing views
            topicsContainer.removeAllViews()
            
            // Get screen dimensions for responsive design
            val displayMetrics = resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            val density = displayMetrics.density
            
            // Determine if it's a tablet (width > 600dp)
            val isTablet = screenWidth / density > 600
            
            // Dynamic spacing based on screen size
            val cardSpacing = if (isTablet) 24 else 16
            val rowSpacing = if (isTablet) 40 else 32
            val cardPadding = if (isTablet) 32 else 24
            
            // Create topic cards in horizontal rows (2 per row)
            for (i in topics.indices step 2) {
                try {
                    val rowLayout = LinearLayout(this).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            if (i > 0) topMargin = (rowSpacing * density).toInt()
                        }
                    }
                    
                    // First card in row
                    val firstTopic = topics[i]
                    if (firstTopic != null) {
                        val firstCard = createTopicCard(firstTopic, i < topics.size - 1, isTablet, cardSpacing, cardPadding)
                        rowLayout.addView(firstCard)
                    }
                    
                    // Second card in row (if exists)
                    if (i + 1 < topics.size) {
                        val secondTopic = topics[i + 1]
                        if (secondTopic != null) {
                            val secondCard = createTopicCard(secondTopic, false, isTablet, cardSpacing, cardPadding)
                            rowLayout.addView(secondCard)
                        }
                    }
                    
                    topicsContainer.addView(rowLayout)
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating row $i", e)
                    // Continue with next row instead of crashing
                    continue
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating topic cards", e)
            showError("Error creating topic cards")
        }
    }
    
    private fun createTopicCard(topic: Topic, hasMarginEnd: Boolean, isTablet: Boolean, cardSpacing: Int, cardPadding: Int): CardView {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                if (hasMarginEnd) marginEnd = (cardSpacing * resources.displayMetrics.density).toInt()
                else marginStart = (cardSpacing * resources.displayMetrics.density).toInt()
                topMargin = (8 * resources.displayMetrics.density).toInt()      // Vertical gap between cards
                bottomMargin = (8 * resources.displayMetrics.density).toInt()   // Vertical gap between cards
            }
            
            // Set CardView radius to 0 to avoid conflicts with custom background
            radius = 0f
            cardElevation = if (isTablet) 12f else 8f  // Better shadow on tablets
            isClickable = true
            isFocusable = true
            
            setOnClickListener {
                try {
                    startTopicActivity(topic.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting topic activity", e)
                    Toast.makeText(context, "Error opening topic", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(cardPadding, cardPadding, cardPadding, cardPadding)
            
            // Topic icon with dynamic sizing
            val iconText = TextView(this@MainActivity).apply {
                text = topic.icon
                textSize = if (isTablet) 48f else 32f  // Bigger icons on tablets
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = (16 * resources.displayMetrics.density).toInt()
                }
            }
            
            // Topic name with dynamic sizing
            val nameText = TextView(this@MainActivity).apply {
                text = topic.name
                textSize = if (isTablet) 24f else 18f  // Bigger text on tablets
                setTextColor(ContextCompat.getColor(context, R.color.white))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            
            addView(iconText)
            addView(nameText)
        }
        
        // Set background color with rounded corners
        try {
            val backgroundColor = android.graphics.Color.parseColor(topic.color)
            
            // Create a rounded background drawable with more pronounced curves
            val roundedBackground = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = if (isTablet) 48f else 36f  // Increased corner radius for better curves
                setColor(backgroundColor)
            }
            
            cardView.background = roundedBackground
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing topic color: ${topic.color}", e)
            val fallbackBackground = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = if (isTablet) 48f else 36f  // Increased corner radius for better curves
                setColor(android.graphics.Color.parseColor("#FF6200EE"))
            }
            cardView.background = fallbackBackground
        }
        
        cardView.addView(contentLayout)
        return cardView
    }
    
    private fun startTopicActivity(topic: String) {
        try {
            Log.d(TAG, "Starting TopicActivity for topic: $topic")
            
            if (topic.isBlank()) {
                Log.e(TAG, "Topic parameter is blank")
                Toast.makeText(this, "Invalid topic selected", Toast.LENGTH_SHORT).show()
                return
            }
            
            val intent = Intent(this, com.v7techsolution.interviewfire.TopicActivity::class.java)
            intent.putExtra("topic", topic)
            startActivity(intent)
            
            Log.d(TAG, "TopicActivity started successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error starting topic activity", e)
            Toast.makeText(this, "Error opening topic: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showError(message: String) {
        try {
            if (topicsContainer != null) {
                topicsContainer.removeAllViews()
                
                // Get screen dimensions for responsive design
                val displayMetrics = resources.displayMetrics
                val density = displayMetrics.density
                val isTablet = displayMetrics.widthPixels / density > 600
                
                // Dynamic sizing based on screen size
                val padding = if (isTablet) 64 else 32
                val iconSize = if (isTablet) 64f else 48f
                val titleSize = if (isTablet) 24f else 18f
                val messageSize = if (isTablet) 20f else 16f
                
                val errorLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                    setPadding((padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt())
                }
                
                val errorIcon = TextView(this).apply {
                    text = "❌"
                    textSize = iconSize
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = (16 * density).toInt()
                    }
                    gravity = android.view.Gravity.CENTER
                }
                
                val errorMessage = TextView(this).apply {
                    text = message
                    textSize = messageSize
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                }
                
                errorLayout.addView(errorIcon)
                errorLayout.addView(errorMessage)
                topicsContainer.addView(errorLayout)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing error message", e)
        }
    }

    private fun showBackendError(message: String) {
        try {
            if (topicsContainer != null) {
                topicsContainer.removeAllViews()
                
                // Get screen dimensions for responsive design
                val displayMetrics = resources.displayMetrics
                val density = displayMetrics.density
                val isTablet = displayMetrics.widthPixels / density > 600
                
                // Dynamic sizing based on screen size
                val padding = if (isTablet) 64 else 32
                val iconSize = if (isTablet) 64f else 48f
                val titleSize = if (isTablet) 24f else 18f
                val messageSize = if (isTablet) 20f else 16f
                
                val errorLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                    setPadding((padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt())
                }
                
                val errorIcon = TextView(this).apply {
                    text = "⚠️" // Warning icon
                    textSize = iconSize
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = (16 * density).toInt()
                    }
                    gravity = android.view.Gravity.CENTER
                }
                
                val errorMessage = TextView(this).apply {
                    text = message
                    textSize = messageSize
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                }
                
                errorLayout.addView(errorIcon)
                errorLayout.addView(errorMessage)
                topicsContainer.addView(errorLayout)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing backend error message", e)
        }
    }

    private fun showNetworkError(message: String) {
        try {
            if (topicsContainer != null) {
                topicsContainer.removeAllViews()
                
                // Get screen dimensions for responsive design
                val displayMetrics = resources.displayMetrics
                val density = displayMetrics.density
                val isTablet = displayMetrics.widthPixels / density > 600
                
                // Dynamic sizing based on screen size
                val padding = if (isTablet) 64 else 32
                val iconSize = if (isTablet) 64f else 48f
                val titleSize = if (isTablet) 24f else 18f
                val messageSize = if (isTablet) 20f else 16f
                
                val errorLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                    setPadding((padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt())
                }
                
                val errorIcon = TextView(this).apply {
                    text = "📡" // Network icon
                    textSize = iconSize
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = (16 * density).toInt()
                    }
                    gravity = android.view.Gravity.CENTER
                }
                
                val errorMessage = TextView(this).apply {
                    text = message
                    textSize = messageSize
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                }
                
                errorLayout.addView(errorIcon)
                errorLayout.addView(errorMessage)
                topicsContainer.addView(errorLayout)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error showing network error message", e)
        }
    }
} 
