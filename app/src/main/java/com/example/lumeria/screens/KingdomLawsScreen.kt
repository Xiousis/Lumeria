package com.example.lumeria.screens

import com.example.lumeria.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.KingdomLaw
import com.example.lumeria.database.KingdomLawDatabase
import com.example.lumeria.database.KingdomRankDatabase

@Composable
fun KingdomLawsScreen(
    playerData: PlayerData,
    onSelectLaw: (Int?) -> Unit,
    onReturn: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        Image(
            painter = painterResource(id = R.drawable.royal_court_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.15f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Royal Proclamations",
                style = MaterialTheme.typography.displaySmall,
                color = Color(0xFFFACC15),
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "As a respected figure of the realm, you may advise the King on national policy. Choose a law to shape Lumeria's future.",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // REPEAL BUTTON
            if (playerData.activeKingdomLawId != null) {
                Button(
                    onClick = { onSelectLaw(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C)),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    Text("REPEAL CURRENT LAW", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val currentRankId = KingdomRankDatabase.getCurrentRank(playerData.renown).id

                items(KingdomLawDatabase.laws) { law ->
                    val isLocked = currentRankId < law.requiredRank
                    val isActive = playerData.activeKingdomLawId == law.id

                    LawCard(
                        law = law,
                        isLocked = isLocked,
                        isActive = isActive,
                        onSelect = { if (!isLocked) onSelectLaw(law.id) }
                    )
                }
            }

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
fun LawCard(
    law: KingdomLaw,
    isLocked: Boolean,
    isActive: Boolean,
    onSelect: () -> Unit
) {
    val cardColor = if (isActive) Color(0xFF1E293B) else if (isLocked) Color(0xFF0F172A).copy(alpha = 0.5f) else Color(0xFF1E293B).copy(alpha = 0.7f)
    val borderColor = if (isActive) Color(0xFFFACC15) else if (isLocked) Color.DarkGray else Color.Gray.copy(alpha = 0.3f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !isLocked && !isActive) { onSelect() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = law.name,
                    color = if (isLocked) Color.Gray else Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isLocked) {
                    Text("🔒 Rank ${law.requiredRank}", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                } else if (isActive) {
                    Text("ACTIVE", color = Color(0xFFFACC15), fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium)
                }
            }

            Text(
                text = law.description,
                color = if (isLocked) Color.DarkGray else Color.LightGray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PRO:", color = Color.Green, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text(law.positiveEffect, color = if (isLocked) Color.DarkGray else Color.White, style = MaterialTheme.typography.bodySmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("CON:", color = Color.Red, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text(law.tradeOff, color = if (isLocked) Color.DarkGray else Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
