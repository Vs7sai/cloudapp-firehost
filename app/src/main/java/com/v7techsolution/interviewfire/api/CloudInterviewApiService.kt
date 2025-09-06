package com.v7techsolution.interviewfire.api

import com.v7techsolution.interviewfire.api.models.QuestionsResponse
import com.v7techsolution.interviewfire.api.models.TopicsResponse
import com.v7techsolution.interviewfire.api.models.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CloudInterviewApiService {
    
    // Protected static JSON endpoints (served through Express.js with authentication)
    @GET("api/topics.json")
    fun getTopics(): Call<TopicsResponse>
    
    @GET("api/questions/{topicId}/{difficulty}.json")
    fun getQuestions(
        @Path("topicId") topicId: String,
        @Path("difficulty") difficulty: String
    ): Call<QuestionsResponse>
    
    // Alternative API endpoints (non-static)
    @GET("api/topics")
    fun getTopicsApi(): Call<TopicsResponse>
    
    @GET("api/questions/{topicId}/{difficulty}")
    fun getQuestionsApi(
        @Path("topicId") topicId: String,
        @Path("difficulty") difficulty: String
    ): Call<QuestionsResponse>
    
    // Public health check endpoint
    @GET("api/health")
    fun getHealth(): Call<ApiResponse>
    
    // Test authentication endpoint
    @GET("api/test-auth")
    fun testAuth(): Call<ApiResponse>
} 