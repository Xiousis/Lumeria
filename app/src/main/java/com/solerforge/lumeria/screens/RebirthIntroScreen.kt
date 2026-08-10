package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.database.RaceDatabase
import kotlinx.coroutines.delay

@Composable
fun RebirthIntroScreen(
    playerName: String,
    playerRace: String,
    onContinue: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    
    val isMonster = RaceDatabase.getRace(playerRace).isMonster
    
    LaunchedEffect(Unit) {
        delay(1000)
        step = 1
        delay(3000)
        step = 2
        delay(3000)
        step = 3
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step >= 1) {
                Text(
                    text = "Goodbye, weary traveler...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light
                )
            }

            if (step >= 2) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Goodbye to your old life...\nYour deeds are etched into history.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )
            }

            if (step >= 3) {
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "HELLO TO YOUR NEW JOURNEY",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = if (isMonster) "Rise, $playerName, and let the world tremble." else "Rise, $playerName, as a new hero of the land.",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Cyan,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(64.dp))

                RpgButton(
                    text = "BEGIN JOURNEY",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}
