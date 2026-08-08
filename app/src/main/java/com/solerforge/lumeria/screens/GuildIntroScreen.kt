package com.solerforge.lumeria.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.database.GuildDatabase
import kotlinx.coroutines.delay

@Composable
fun GuildIntroScreen(
    guildName: String,
    onContinue: () -> Unit
) {
    val history = GuildDatabase.getHouseHistory(guildName)
    val scrollState = rememberScrollState()
    
    val guildBg = when (guildName) {
        "House of Fire" -> R.drawable.house_of_fire_joining
        "House of Water" -> R.drawable.house_of_water_joining
        "House of Wind" -> R.drawable.house_of_wind_joining
        else -> R.drawable.guild_screen_bg
    }
    
    val guildColor = when (guildName) {
        "House of Fire" -> Color(0xFFEF4444)
        "House of Water" -> Color(0xFF3B82F6)
        "House of Wind" -> Color(0xFF10B981)
        else -> Color.Cyan
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        val theme = when (guildName) {
            "House of Fire" -> R.raw.house_of_fire_guild_theme
            "House of Water" -> R.raw.house_of_water_guild_theme
            "House of Wind" -> R.raw.house_of_wind_ninja_guild_theme
            else -> R.raw.lumeria_main_menu_theme
        }
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, theme)
    }

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        delay(1000)
        while (scrollState.value < scrollState.maxValue) {
            scrollState.animateScrollTo(
                value = scrollState.value + 1,
                animationSpec = tween(durationMillis = 20, easing = LinearEasing)
            )
        }
        delay(2000)
        onContinue()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = guildBg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Lightened gradient for better background art visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.5f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = guildName.uppercase(),
                color = guildColor,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, top = 24.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    
                    Text(
                        text = history,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 400.dp)
                    )
                }
            }
            
            Text(
                text = "Tap to skip...",
                color = Color.Gray.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 16.dp).clickable { onContinue() }
            )
        }
    }
}
