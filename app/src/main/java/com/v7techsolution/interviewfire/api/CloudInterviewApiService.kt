package com.v7techsolution.interviewfire.api

import com.v7techsolution.interviewfire.api.models.QuestionsResponse
import com.v7techsolution.interviewfire.api.models.TopicsResponse
import com.v7techsolution.interviewfire.api.models.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CloudInterviewApiService {
    
    // Static JSON endpoints (Firebase Hosting) - No authentication required for static files
    @GET("api/topics.json")
    fun getTopics(): Call<TopicsResponse>
    
    @GET("api/questions/{topicId}/{difficulty}.json")
    fun getQuestions(
        @Path("topicId") topicId: String,
        @Path("difficulty") difficulty: String
    ): Call<QuestionsResponse>
    
    // Public endpoints (no authentication required) - Static JSON endpoints
    @GET("api/health.json")
    fun getHealth(): Call<ApiResponse>
} 