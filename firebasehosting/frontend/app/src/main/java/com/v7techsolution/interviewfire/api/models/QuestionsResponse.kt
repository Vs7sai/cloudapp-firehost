package com.v7techsolution.interviewfire.api.models

data class QuestionsResponse(
    val success: Boolean,
    val data: QuestionsData,
    val user: User?
)

data class QuestionsData(
    val topic: String,
    val difficulty: String,
    val questions: List<Question>
)

data class Question(
    val id: Int,
    val text: String,
    val textSize: Int,
    val explanation: String?
)
