package com.v7techsolution.interviewfire.api.models

data class TopicsResponse(
    val success: Boolean,
    val data: List<Topic>,
    val user: User?
)

data class User(
    val email: String,
    val name: String,
    val uid: String?,
    val emailVerified: Boolean?
)
