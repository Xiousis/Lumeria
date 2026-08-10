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

import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.solerforge.lumeria.database.RaceDatabase
import com.solerforge.lumeria.models.ChatChannel
import com.solerforge.lumeria.models.ChatMessage

@Composable
fun WorldChatScreen(
    playerData: PlayerData,
    viewModel: com.solerforge.lumeria.viewmodels.WorldChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onReturn: () -> Unit
) {
    var textState by remember { mutableStateOf("") }
    val race = RaceDatabase.getRace(playerData.playerRace)
    val isMonster = playerData.isReborn && race.isMonster
    val currentChannel = viewModel.currentChannel
    val chatMessages = viewModel.messages.filter { it.channel == currentChannel }

    val availableChannels = remember(isMonster) {
        if (isMonster) {
            listOf(ChatChannel.WORLD, ChatChannel.MONSTERS_DEN)
        } else {
            listOf(ChatChannel.WORLD, ChatChannel.HEROES_TAVERN)
        }
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
                    text = if (isMonster) "Monsters' Den [ABYSS]" else "Heroes' Tavern [VALOR]",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isMonster) Color.Red else Color.Cyan,
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

            // CHANNEL TABS
            TabRow(
                selectedTabIndex = availableChannels.indexOf(currentChannel).coerceAtLeast(0),
                containerColor = Color.Transparent,
                contentColor = Color.Cyan,
                divider = {},
                indicator = { tabPositions ->
                    if (availableChannels.indexOf(currentChannel) >= 0) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[availableChannels.indexOf(currentChannel)]),
                            color = if (isMonster && currentChannel == ChatChannel.MONSTERS_DEN) Color.Red else Color.Cyan
                        )
                    }
                }
            ) {
                availableChannels.forEach { channel ->
                    Tab(
                        selected = currentChannel == channel,
                        onClick = { viewModel.setChannel(channel) },
                        text = {
                            Text(
                                channel.displayName,
                                color = if (currentChannel == channel) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    placeholder = { 
                        val placeholderText = when(currentChannel) {
                            ChatChannel.WORLD -> "Speak to the world..."
                            ChatChannel.MONSTERS_DEN -> "Grumble in the shadows..."
                            ChatChannel.HEROES_TAVERN -> "Share a tale of valor..."
                        }
                        Text(placeholderText, color = Color.Gray) 
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.DarkGray.copy(alpha = 0.5f),
                        unfocusedContainerColor = Color.DarkGray.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = if (isMonster) Color.Red else Color.Cyan
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (textState.isNotBlank()) {
                            viewModel.sendMessage(playerData.playerName, textState, currentChannel)
                            textState = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(if (isMonster && currentChannel == ChatChannel.MONSTERS_DEN) Color.Red else Color.Cyan, CircleShape)
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
