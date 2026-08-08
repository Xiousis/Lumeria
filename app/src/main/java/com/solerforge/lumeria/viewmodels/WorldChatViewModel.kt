package com.solerforge.lumeria.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.solerforge.lumeria.screens.ChatMessage

class WorldChatViewModel : ViewModel() {
    val messages = mutableStateListOf<ChatMessage>(
        ChatMessage("King Alaric", "Welcome to the Heroes' Circle. May your new journey be legendary.", Color.Yellow),
        ChatMessage("Ignis", "A new spark appears. Let's see if you can keep that fire burning.", Color(0xFFEF4444)),
        ChatMessage("Marina", "The tides have brought you back to us. Welcome home, hero.", Color(0xFF3B82F6)),
        ChatMessage("Zephyr", "I see you. The wind always carries the scent of potential.", Color(0xFF10B981)),
        ChatMessage("Billy", "A fresh start, eh? I've got plenty of gear for a rising star like you!", Color.Cyan),
        ChatMessage("Shadow Stalker", "They say those who are reborn see the world differently. What do you see?", Color.Magenta)
    )

    init {
        // Future: Add Firestore listener here to sync messages globally
        // FirebaseFirestore.getInstance().collection("world_chat")
        //    .orderBy("timestamp", Query.Direction.DESCENDING).limit(50)
        //    .addSnapshotListener { snapshot, _ -> ... }
    }

    fun sendMessage(sender: String, text: String) {
        if (text.isNotBlank()) {
            val newMessage = ChatMessage(sender, text, Color.Cyan, true)
            messages.add(newMessage)
            // Future: Upload to Firestore here
        }
    }
}
