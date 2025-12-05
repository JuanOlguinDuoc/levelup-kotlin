package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class PlatformModel(
    val id: Long,
    val name: String
)