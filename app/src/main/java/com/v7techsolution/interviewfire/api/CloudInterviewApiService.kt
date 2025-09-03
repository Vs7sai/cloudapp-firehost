package com.v7techsolution.interviewfire.api

import com.v7techsolution.interviewfire.api.models.QuestionsResponse
import com.v7techsolution.interviewfire.api.models.TopicsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CloudInterviewApiService {
    
    // Protected endpoints (require authentication)
    @GET("api/topics")
    fun getTopics(): Call<TopicsResponse>
    
    @GET("api/topics/{topicId}")
    fun getTopic(@Path("topicId") topicId: String): Call<TopicsResponse>
    
    @GET("api/questions/{topicId}/{difficulty}")
    fun getQuestions(
        @Path("topicId") topicId: String,
        @Path("difficulty") difficulty: String
    ): Call<QuestionsResponse>
    
    @GET("api/questions/{topicId}")
    fun getAllQuestions(@Path("topicId") topicId: String): Call<QuestionsResponse>
    
    @GET("api/test-auth")
    fun testAuth(): Call<ApiResponse>
    
    // Public endpoints (no authentication required)
    @GET("api/health")
    fun getHealth(): Call<ApiResponse>
    
    @GET("api/gateway/status")
    fun getGatewayStatus(): Call<ApiResponse>
    
    @GET("api/gateway/info")
    fun getGatewayInfo(): Call<ApiResponse>
    
    @GET("/")
    fun getApiInfo(): Call<ApiResponse>
} 