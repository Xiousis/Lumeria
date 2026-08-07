package com.example.lumeria.screens

import com.example.lumeria.R

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.Fish
import com.example.lumeria.database.FishDatabase
import com.example.lumeria.utils.CurrencyUtils
import com.example.lumeria.utils.RarityUtils
import kotlinx.coroutines.delay

enum class FishingState {
    Idle, Casting, Bite, Caught, Missed
}

@Composable
fun FishingScreen(
    playerData: PlayerData,
    onFishCaught: (Fish) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var gameState by remember { mutableStateOf(FishingState.Idle) }
    var currentFish by remember { mutableStateOf<Fish?>(null) }
    var biteTimestamp by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        com.example.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }
    var feedbackMessage by remember(playerData.equippedRod) { 
        mutableStateOf(if (playerData.equippedRod == "None") "You need a Fishing Rod to start!" else "Ready to cast your line?") 
    }

    val rod = remember(playerData.equippedRod) { com.example.lumeria.database.GameDatabase.getFishingRod(playerData.equippedRod) }
    val reactionWindow = (1000 + (playerData.fishingLevel * 10) + rod.reactionBonus).toLong()

    LaunchedEffect(gameState) {
        if (gameState == FishingState.Casting) {
            feedbackMessage = "Waiting for a bite..."
            val waitTime = (2000..5000).random().toLong()
            delay(waitTime)
            gameState = FishingState.Bite
            biteTimestamp = System.currentTimeMillis()
            feedbackMessage = "BITE! REEL IT IN!"
        } else if (gameState == FishingState.Bite) {
            delay(reactionWindow)
            if (gameState == FishingState.Bite) {
                gameState = FishingState.Missed
                feedbackMessage = "The fish got away..."
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.fishing_pond_v2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Darker Overlay for UI readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Lumeria Fishing Hole",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Cyan,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Fishing Level ${playerData.fishingLevel}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            // REACTION INDICATOR
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.2f))
                    .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (gameState == FishingState.Bite) {
                    val infiniteTransition = rememberInfiniteTransition(label = "BitePulse")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.3f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "PulseScale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .background(Color.Yellow, CircleShape)
                            .clickable {
                                val reactionTime = System.currentTimeMillis() - biteTimestamp
                                if (reactionTime <= reactionWindow) {
                                    val fish = FishDatabase.rollFish(playerData.fishingLevel)
                                    currentFish = fish
                                    gameState = FishingState.Caught
                                    onFishCaught(fish)
                                    feedbackMessage = "You caught a ${fish.name}!"
                                }
                            }
                    ) {
                        Text(
                            "TAP!", 
                            modifier = Modifier.align(Alignment.Center), 
                            fontWeight = FontWeight.Black, 
                            color = Color.Black
                        )
                    }
                } else if (gameState == FishingState.Casting) {
                    CircularProgressIndicator(color = Color.Cyan)
                } else {
                    if (playerData.equippedRod == "None") {
                        Text(text = "❌", fontSize = 64.sp)
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.fish_icon),
                            contentDescription = "Fish",
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (playerData.equippedRod != "None") {
                Text(
                    text = "Equipped: ${playerData.equippedRod}",
                    color = Color.Green,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = feedbackMessage,
                style = MaterialTheme.typography.headlineSmall,
                color = when(gameState) {
                    FishingState.Bite -> Color.Yellow
                    FishingState.Caught -> Color.Green
                    FishingState.Missed -> Color.Red
                    else -> Color.White
                },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            if (gameState == FishingState.Idle || gameState == FishingState.Caught || gameState == FishingState.Missed) {
                Image(
                    painter = painterResource(id = R.drawable.cast_line_font),
                    contentDescription = "Cast Line",
                    modifier = Modifier
                        .height(80.dp)
                        .clickable(enabled = playerData.equippedRod != "None") {
                            currentFish = null
                            gameState = FishingState.Casting
                        },
                    contentScale = ContentScale.Fit,
                    alpha = if (playerData.equippedRod != "None") 1f else 0.4f
                )
            } else {
                Spacer(modifier = Modifier.height(80.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
        
        // CATCH OVERLAY
        AnimatedVisibility(
            visible = gameState == FishingState.Caught,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            currentFish?.let { fish ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { gameState = FishingState.Idle },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xFF111827), RoundedCornerShape(24.dp))
                            .border(2.dp, RarityUtils.getColor(fish.rarity), RoundedCornerShape(24.dp))
                            .padding(32.dp)
                    ) {
                        Text("YOU CAUGHT", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        Text(
                            fish.name, 
                            color = RarityUtils.getColor(fish.rarity), 
                            style = MaterialTheme.typography.headlineLarge, 
                            fontWeight = FontWeight.Black
                        )
                        Text(fish.rarity, color = RarityUtils.getColor(fish.rarity))
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(fish.description, color = Color.LightGray, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Value: ${CurrencyUtils.formatGold(fish.basePrice)}", color = Color.Yellow)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Text("Tap to continue", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
