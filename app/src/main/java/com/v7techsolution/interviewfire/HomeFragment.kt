package com.v7techsolution.interviewfire

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.v7techsolution.interviewfire.api.ApiClient
import com.v7techsolution.interviewfire.api.models.Topic
import com.v7techsolution.interviewfire.api.models.TopicsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.v7techsolution.interviewfire.api.CloudInterviewApiService

class HomeFragment : Fragment() {

    private lateinit var topicsContainer: LinearLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        initializeViews(view)

        // Setup pull-to-refresh
        setupSwipeRefresh()

        // Load topics from backend
        loadTopicsFromBackend()
    }

    private fun initializeViews(view: View) {
        Log.d(TAG, "Initializing HomeFragment views...")

        try {
            // Main views
            topicsContainer = view.findViewById(R.id.topics_container)
            swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

            Log.d(TAG, "HomeFragment views initialized successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing HomeFragment views", e)
            Toast.makeText(context, "Error initializing views: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupSwipeRefresh() {
        try {
            swipeRefreshLayout.setOnRefreshListener {
                // Show refresh indicator
                swipeRefreshLayout.isRefreshing = true

                // Reload topics from backend
                loadTopicsFromBackend()
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
            // Create API client with dynamic base URL
            val retrofit = ApiClient.createRetrofit(requireContext())
            val apiService = retrofit.create(CloudInterviewApiService::class.java)

            Log.d(TAG, "Making API call to getTopics()...")
            apiService.getTopics().enqueue(object : Callback<TopicsResponse> {
                override fun onResponse(call: Call<TopicsResponse>, response: Response<TopicsResponse>) {
                    Log.d(TAG, "API Response received: ${response.code()}")
                    Log.d(TAG, "Response body: ${response.body()}")

                    // Stop refresh indicator
                    try {
                        swipeRefreshLayout?.isRefreshing = false
                    } catch (e: Exception) {
                        Log.e(TAG, "Error stopping refresh", e)
                    }

                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val topicsResponse = response.body()!!
                            Log.d(TAG, "Topics response success: ${topicsResponse.success}")
                            Log.d(TAG, "Topics data size: ${topicsResponse.data.size}")

                            if (topicsResponse.success && topicsResponse.data.isNotEmpty()) {
                                Log.d(TAG, "Topics loaded successfully: ${topicsResponse.data.size}")
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
                    Log.e(TAG, "API Call Failed with exception: ${t.javaClass.simpleName}")
                    Log.e(TAG, "Error message: ${t.message}")
                    Log.e(TAG, "Full stack trace:", t)

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
                    val rowLayout = LinearLayout(requireContext()).apply {
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
        val cardView = CardView(requireContext()).apply {
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
                    (activity as? MainActivity)?.startTopicActivity(topic.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting topic activity", e)
                    Toast.makeText(context, "Error opening topic", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val contentLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(cardPadding, cardPadding, cardPadding, cardPadding)

            // Topic icon with dynamic sizing
            val iconText = TextView(requireContext()).apply {
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
            val nameText = TextView(requireContext()).apply {
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

                val errorLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                    setPadding((padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt())
                }

                val errorIcon = TextView(requireContext()).apply {
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

                val errorMessage = TextView(requireContext()).apply {
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

                val errorLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    gravity = android.view.Gravity.CENTER
                    setPadding((padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt(), (padding * density).toInt())
                }

                val errorIcon = TextView(requireContext()).apply {
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

                val errorMessage = TextView(requireContext()).apply {
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
}