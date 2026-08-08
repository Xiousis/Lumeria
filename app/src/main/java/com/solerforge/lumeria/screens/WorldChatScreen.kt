package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.data.PlayerData

data class ChatMessage(val sender: String, val message: String, val color: Color, val isMe: Boolean = false)

@Composable
fun WorldChatScreen(
    playerData: PlayerData,
    onReturn: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("King Alaric", "Welcome to the Heroes' Circle, ${playerData.playerName}. May your new journey be legendary.", Color.Yellow),
            ChatMessage("Ignis", "A new spark appears. Let's see if you can keep that fire burning.", Color(0xFFEF4444)),
            ChatMessage("Marina", "The tides have brought you back to us. Welcome home, hero.", Color(0xFF3B82F6)),
            ChatMessage("Zephyr", "I see you. The wind always carries the scent of potential.", Color(0xFF10B981)),
            ChatMessage("Billy", "A fresh start, eh? I've got plenty of gear for a rising star like you!", Color.Cyan),
            ChatMessage("Shadow Stalker", "They say those who are reborn see the world differently. What do you see?", Color.Magenta)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORLD CHAT [HEROES]",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Cyan,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "LVL ${playerData.level}",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages) { msg ->
                    ChatBubble(msg)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INPUT AREA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textState,
                    onValueChange = { textState = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Speak to the Legends...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.DarkGray.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.Cyan
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (textState.isNotBlank()) {
                            chatMessages.add(ChatMessage(playerData.playerName, textState, Color.Cyan, true))
                            textState = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Cyan, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_send),
                        contentDescription = "Send",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .height(60.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = msg.sender,
            color = msg.color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .background(
                    if (msg.isMe) Color.Cyan.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.4f),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    if (msg.isMe) Color.Cyan else Color.Gray.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(text = msg.message, color = Color.White, fontSize = 14.sp)
        }
    }
}
