package com.example.levelup.model

import kotlinx.serialization.Serializable

@Serializable
data class RoleModel(
    val id: Long? = null,
    val name: String? = null
)
