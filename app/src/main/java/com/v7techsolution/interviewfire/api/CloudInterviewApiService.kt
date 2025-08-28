package com.v7techsolution.interviewfire.api

import com.v7techsolution.interviewfire.api.models.QuestionsResponse
import com.v7techsolution.interviewfire.api.models.TopicsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CloudInterviewApiService {
    
    @GET("api/topics")
    fun getTopics(): Call<TopicsResponse>
    
    @GET("api/topics/{topicId}")
    fun getTopic(@Path("topicId") topicId: String): Call<TopicsResponse>
    
    @GET("api/questions/{topicId}/{difficulty}")
    fun getQuestions(
        @Path("topicId") topicId: String,
        @Path("difficulty") difficulty: String
    ): Call<QuestionsResponse>
} 