package com.solerforge.lumeria.data

import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val hapticsEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val cloudSaveEnabled: Boolean = false
)
