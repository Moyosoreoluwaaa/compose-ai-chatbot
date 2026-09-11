package com.example.aichatbot.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val profilePicUrl: String? = null,
    val googleId: String,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val tokenExpiresAt: Long? = null,
    val createdAt: Long,
    val lastSignIn: Long,
    val isActive: Boolean = true
)
