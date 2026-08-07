package com.example.lumeria.models

import kotlinx.serialization.Serializable

@Serializable
data class Meal(
    val name: String,
    val description: String,
    val price: Int,
    val buff: PlayerBuff
)
