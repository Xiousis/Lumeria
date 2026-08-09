package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.utils.MusicManager

@Composable
fun DefeatedScreen(goldLost: Long = 0, onReturnToMenu: () -> Unit) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_defeated_sting)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.defeated_screen),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "The journey is long and fraught with peril. A true warrior never gives up.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )

            if (goldLost > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Penalty: Lost ${CurrencyUtils.formatGold(goldLost)} (15%)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Yellow,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_game_menu_font),
                contentDescription = "Return to Game Menu",
                modifier = Modifier
                    .height(120.dp)
                    .clickable { onReturnToMenu() },
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
