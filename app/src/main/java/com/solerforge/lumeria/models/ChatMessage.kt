package com.solerforge.lumeria.models

import androidx.compose.ui.graphics.Color

data class ChatMessage(
    val sender: String,
    val message: String,
    val color: Color,
    val isMe: Boolean = false,
    val channel: ChatChannel = ChatChannel.WORLD
)
