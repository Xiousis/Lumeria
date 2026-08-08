package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.WorldDatabase
import com.solerforge.lumeria.components.LocationRow
import com.solerforge.lumeria.utils.MusicManager

@Composable
fun WorldMapScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onTravel: (String) -> Unit,
    onBossBattle: (String) -> Unit,
    onReturn: () -> Unit
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Full-screen Map Background
        Image(
            painter = painterResource(id = R.drawable.main_map_v2),
            contentDescription = "World Map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f // Made more faded as requested
        )

        // UI Layer
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.world_map_text),
                    contentDescription = "World Map",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(0.8f),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                val visibleLocations = if (playerData.isReborn) {
                    WorldDatabase.legacyLocations
                } else {
                    WorldDatabase.locations.filter { !it.isLegacyOnly }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(visibleLocations) { location ->
                        val isUnlocked = (playerData.level >= location.requiredLevel) || (location.name in playerData.unlockedLocations)
                        val isCurrent = location.name == playerData.currentLocation
                        val hasIncursion = location.name == playerData.voidIncursionLocation
                        
                        LocationRow(
                            location = location, 
                            isUnlocked = isUnlocked, 
                            isCurrent = isCurrent,
                            playerDefeatedBosses = playerData.defeatedBosses,
                            isVoidIncursion = hasIncursion,
                            onTravel = { onTravel(location.name) },
                            onBossBattle = { onBossBattle(location.name) }
                        )
                    }
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
}
