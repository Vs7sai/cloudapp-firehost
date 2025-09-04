package com.v7techsolution.interviewfire.api.models

data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null,
    val data: Any? = null,
    val timestamp: String? = null,
    val uptime: Long? = null
)