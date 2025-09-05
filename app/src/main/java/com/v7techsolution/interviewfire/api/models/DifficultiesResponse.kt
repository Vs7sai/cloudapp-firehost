package com.v7techsolution.interviewfire.api.models

data class DifficultiesResponse(
    val success: Boolean,
    val data: Map<String, List<String>>,
    val message: String? = null,
    val last_updated: String? = null
)
