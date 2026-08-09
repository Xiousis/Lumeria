package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.managers.RebirthManager
import com.solerforge.lumeria.components.RpgButton

@Composable
fun RebirthRequirementsScreen(
    playerData: PlayerData,
    onStartRebirth: () -> Unit,
    onReturn: () -> Unit
) {
    val requirements = RebirthManager.getRequirements(playerData)
    val canRebirth = RebirthManager.canRebirth(playerData)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (playerData.isReborn) "LEGENDARY HERO" else "THE REBIRTH ALTAR",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Yellow,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (playerData.isReborn) 
                    "You have already transcended your mortal limits. You are now a Legend of Lumeria." 
                    else "Only those who have truly conquered Lumeria may transcend their mortal limits and forge a new legacy.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

            if (!playerData.isReborn) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requirements) { req ->
                        RequirementRow(req)
                    }
                }

                if (canRebirth) {
                    RpgButton(
                        text = "BEGIN ASCENSION",
                        onClick = onStartRebirth,
                        containerColor = Color(0xFF6200EE),
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )
                    Text(
                        text = "WARNING: This will reset your progress to Level 1 and allow you to create a new hero. This cannot be undone.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.DarkGray)
                    ) {
                        Text("REQUIREMENTS NOT MET", color = Color.Gray)
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Your journey as ${playerData.playerName} is permanent.\nContinue your legend through the world of Lumeria.",
                    color = Color.Cyan,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Return",
                color = Color.Cyan,
                modifier = Modifier.clickable { onReturn() }.padding(8.dp)
            )
        }
    }
}

@Composable
fun RequirementRow(req: RebirthManager.RebirthRequirement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (req.isMet) Color.Green.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (req.isMet) Icons.Default.CheckCircle else Icons.Default.Lock,
            contentDescription = null,
            tint = if (req.isMet) Color.Green else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = req.label,
                color = if (req.isMet) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { req.currentProgress.toFloat() / req.targetProgress.toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp).padding(top = 4.dp),
                color = if (req.isMet) Color.Green else Color.Cyan,
                trackColor = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "${req.currentProgress}/${req.targetProgress}",
            color = if (req.isMet) Color.Green else Color.LightGray,
            fontSize = 14.sp
        )
    }
}
