package com.lucyyan.cocmaterials.exception

import java.time.Instant

data class ApiError(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: Instant
)