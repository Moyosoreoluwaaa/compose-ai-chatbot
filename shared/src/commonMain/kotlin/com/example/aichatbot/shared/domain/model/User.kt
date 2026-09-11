package com.example.aichatbot.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val profilePicUrl: String? = null,
    val googleId: String,
    val createdAt: Long,
    val lastSignIn: Long
)

@Serializable
data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val accessToken: String? = null,
    val error: String? = null
)
