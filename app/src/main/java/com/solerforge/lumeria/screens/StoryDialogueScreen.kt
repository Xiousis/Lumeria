package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.models.StoryEvent

@Composable
fun StoryDialogueScreen(
    arc: StoryArc,
    playerName: String = "Xious",
    eventIndex: Int,
    onNext: () -> Unit,
    onChoiceSelected: (com.solerforge.lumeria.models.StoryChoiceOption) -> Unit,
    onBattleStart: (String, Boolean) -> Unit,
    onReturn: () -> Unit
) {
    if (eventIndex >= arc.events.size) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Cyan)
        }
        return
    }

    val currentEvent = arc.events[eventIndex]
    var outcomeMessage by remember(eventIndex) { mutableStateOf<String?>(null) }

    // Automatically trigger battle if it's a battle event
    LaunchedEffect(currentEvent) {
        if (currentEvent is StoryEvent.Battle) {
            onBattleStart(currentEvent.enemyName, currentEvent.isBoss)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = arc.backgroundId ?: R.drawable.story_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = 1.2f, scaleY = 1.2f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ARC TITLE
            Text(
                text = arc.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Cyan,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (currentEvent is StoryEvent.Dialogue) {
                // DIALOGUE BOX
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    val rawMessage = outcomeMessage ?: currentEvent.text
                    val rawSpeaker = if (outcomeMessage != null) "Outcome" else currentEvent.speaker
                    
                    val messageToDisplay = rawMessage.replace("{PLAYER_NAME}", playerName)
                    val speakerToDisplay = rawSpeaker.replace("{PLAYER_NAME}", playerName)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if ((currentEvent.speaker == "Xious" || currentEvent.speaker == "{PLAYER_NAME}") && outcomeMessage == null) {
                            XiousPortrait(mood = currentEvent.mood)
                            Spacer(modifier = Modifier.width(16.dp))
                        } else if (outcomeMessage == null) {
                            val portraitRes = when (currentEvent.speaker) {
                                "Village Elder" -> R.drawable.vil_elder
                                "Kaela", "Kaela Flameheart" -> R.drawable.kaela
                                else -> null
                            }

                            if (portraitRes != null) {
                                Image(
                                    painter = painterResource(id = portraitRes),
                                    contentDescription = "${currentEvent.speaker} Portrait",
                                    modifier = Modifier
                                        .size(64.dp)
                                        .then(
                                            if (currentEvent.speaker == "Village Elder") Modifier 
                                            else Modifier.clip(CircleShape).border(2.dp, Color.Yellow.copy(alpha = 0.5f), CircleShape)
                                        ),
                                    contentScale = ContentScale.Fit,
                                    alignment = Alignment.Center
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            val speakerColor = when (speakerToDisplay) {
                                "Xious", playerName -> Color.Cyan
                                "Village Elder" -> Color.Yellow
                                "Kaela", "Kaela Flameheart" -> Color(0xFFFF5722)
                                "The Void" -> Color.Magenta
                                "Outcome" -> Color.Green
                                else -> Color.Yellow
                            }

                            Text(
                                text = speakerToDisplay,
                                color = speakerColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = if (outcomeMessage != null) messageToDisplay else "\"$messageToDisplay\"",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RpgButton(
                            text = if (eventIndex < arc.events.size - 1) "Next" else "Finish Arc",
                            onClick = onNext
                        )
                    }
                }
            } else if (currentEvent is StoryEvent.Choice) {
                // CHOICE BOX
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Yellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentEvent.prompt,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    currentEvent.options.forEach { option ->
                        Button(
                            onClick = { onChoiceSelected(option) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text(option.text, color = Color.White)
                        }
                    }
                }
            } else {
                // Loading Battle State (Briefly shown before LaunchedEffect triggers)
                Text("Preparing for Battle...", color = Color.White)
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
    }
}

@Composable
fun XiousPortrait(mood: String) {
    val bitmap = ImageBitmap.imageResource(id = R.drawable.xious_hp_100)
    
    // Assuming a 4x2 grid of faces in the image
    // You can adjust these if your image layout is different (e.g. 3x3)
    val columns = 1
    val rows = 1
    
    val (col, row) = 0 to 0

    Canvas(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Yellow.copy(alpha = 0.5f), CircleShape)
    ) {
        val srcWidth = bitmap.width / columns
        val srcHeight = bitmap.height / rows
        
        drawImage(
            image = bitmap,
            srcOffset = IntOffset(col * srcWidth, row * srcHeight),
            srcSize = IntSize(srcWidth, srcHeight),
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )
    }
}
