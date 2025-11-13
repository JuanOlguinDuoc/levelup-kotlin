package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class PlatformModel(
    val id: Int,
    val name: String
)