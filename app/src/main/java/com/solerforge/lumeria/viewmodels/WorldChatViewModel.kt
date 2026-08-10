package com.solerforge.lumeria.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.solerforge.lumeria.models.ChatChannel
import com.solerforge.lumeria.models.ChatMessage

class WorldChatViewModel : ViewModel() {
    val messages = mutableStateListOf<ChatMessage>()

    var currentChannel by mutableStateOf(ChatChannel.WORLD)
        private set

    fun setChannel(channel: ChatChannel) {
        currentChannel = channel
    }

    fun sendMessage(sender: String, text: String, channel: ChatChannel) {
        if (text.isNotBlank()) {
            val newMessage = ChatMessage(sender, text, Color.Cyan, true, channel)
            messages.add(newMessage)
        }
    }
}
