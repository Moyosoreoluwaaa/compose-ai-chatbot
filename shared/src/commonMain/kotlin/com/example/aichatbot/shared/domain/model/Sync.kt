package com.example.aichatbot.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncState(
    val sessionId: String,
    val lastSyncTime: Long = 0,
    val isSyncing: Boolean = false,
    val lastError: String? = null,
    val pendingChanges: Int = 0
)

@Serializable
data class CloudSyncConfig(
    val autoSync: Boolean = true,
    val syncInterval: Long = 30000L, // 30 seconds
    val wifiOnly: Boolean = false
)
