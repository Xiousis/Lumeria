package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.Bounty
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.BountyDatabase
import com.solerforge.lumeria.utils.CurrencyUtils

@Composable
fun BountyBoardScreen(
    playerData: PlayerData,
    onAcceptBounty: (Int) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bounty_board_bg),
            contentDescription = "Bounty Board Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
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
                .padding(16.dp)
        ) {
            Text(
                text = "Bounty Board",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFF87171),
                fontWeight = FontWeight.Black
            )

            Text(
                text = "Wanted for crimes against the realm.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val activeId: Int? = playerData.activeBountyId
            val activeBounty = activeId?.let { BountyDatabase.getBounty(it) }
            
            if (activeBounty != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 250.dp)
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bounty_panel),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    
                    Column(
                        modifier = Modifier.padding(horizontal = 90.dp, vertical = 60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ACTIVE TARGET", color = Color(0xFF7F1D1D), fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelSmall)
                        Text(
                            text = activeBounty.targetName, 
                            style = MaterialTheme.typography.headlineMedium, 
                            color = Color(0xFF451A03), 
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Text(
                            text = "LAST SEEN: ${activeBounty.location.uppercase()}", 
                            color = Color(0xFFB45309), 
                            style = MaterialTheme.typography.labelSmall, 
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(BountyDatabase.bounties) { bounty ->
                    val isDefeated = playerData.completedBountyIds.contains(bounty.id)
                    val isTracking = playerData.activeBountyId == bounty.id
                    val noneActive = playerData.activeBountyId == null
                    
                    BountyRowItem(
                        bounty = bounty,
                        isCompleted = isDefeated,
                        isActive = isTracking,
                        canAccept = noneActive && !isDefeated,
                        onAccept = { onAcceptBounty(bounty.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .padding(8.dp)
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun BountyRowItem(
    bounty: Bounty,
    isCompleted: Boolean,
    isActive: Boolean,
    canAccept: Boolean,
    onAccept: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 450.dp) // Even longer to allow for massive padding
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // BACKGROUND PANEL
        Image(
            painter = painterResource(id = R.drawable.bounty_panel),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    alpha = if (isCompleted) 0.4f else 1f
                },
            contentScale = ContentScale.FillBounds
        )

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 100.dp, vertical = 90.dp), // Massive padding to force text into the paper center
            horizontalAlignment = Alignment.CenterHorizontally // Center everything
        ) {
            Text(
                text = bounty.targetName,
                style = MaterialTheme.typography.headlineSmall,
                color = if (isCompleted) Color.Gray else Color(0xFF451A03), 
                fontWeight = FontWeight.ExtraBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Text(
                text = "Lv. ${bounty.level}",
                style = MaterialTheme.typography.titleLarge,
                color = if (isCompleted) Color.Gray else Color(0xFF991B1B),
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = bounty.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCompleted) Color.Gray else Color(0xFF78350F), 
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "LAST SEEN: ${bounty.location.uppercase()}",
                style = MaterialTheme.typography.titleSmall,
                color = if (isCompleted) Color.Gray else Color(0xFFB45309),
                fontWeight = FontWeight.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f)) 

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "REWARD",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCompleted) Color.Gray else Color(0xFF92400E)
                )
                Text(
                    text = CurrencyUtils.formatGold(bounty.rewardGold),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isCompleted) Color.Gray else Color(0xFF15803D),
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (canAccept) {
                    Image(
                        painter = painterResource(id = R.drawable.accept_font),
                        contentDescription = "Accept",
                        modifier = Modifier
                            .height(80.dp) 
                            .clickable { onAccept() },
                        contentScale = ContentScale.Fit
                    )
                } else if (isActive) {
                    Text("HUNTING...", color = Color(0xFFB45309), fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
                } else if (isCompleted) {
                    Text("ELIMINATED", color = Color.Gray, fontWeight = FontWeight.Black, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}
