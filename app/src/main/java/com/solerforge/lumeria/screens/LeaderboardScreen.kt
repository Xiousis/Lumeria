package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import com.solerforge.lumeria.models.LeaderboardEntry

enum class LeaderboardTab { GLOBAL, PVP }

@Composable
fun LeaderboardScreen(
    globalRankings: List<LeaderboardEntry>,
    pvpRankings: List<LeaderboardEntry>,
    isLoading: Boolean,
    onReturn: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(LeaderboardTab.GLOBAL) }

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
                .background(Color.Black.copy(alpha = 0.7f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HALL OF LEGENDS",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Yellow,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedTab = LeaderboardTab.GLOBAL },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == LeaderboardTab.GLOBAL) MaterialTheme.colorScheme.primary else Color.DarkGray
                    )
                ) {
                    Text("GLOBAL (LEVEL)")
                }
                Button(
                    onClick = { selectedTab = LeaderboardTab.PVP },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == LeaderboardTab.PVP) MaterialTheme.colorScheme.primary else Color.DarkGray
                    )
                ) {
                    Text("PvP (WINS)")
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Cyan)
                }
            } else {
                val rankings = if (selectedTab == LeaderboardTab.GLOBAL) globalRankings else pvpRankings
                
                if (rankings.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No data available yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(rankings) { index, entry ->
                            LeaderboardRow(index + 1, entry, selectedTab)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .height(60.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, entry: LeaderboardEntry, tab: LeaderboardTab) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .border(1.dp, if (rank <= 3) rankColor else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Text(
                    text = "#$rank",
                    color = rankColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.width(40.dp)
                )
                
                Column {
                    Text(
                        text = entry.playerName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Lv. ${entry.level} ${entry.playerClass}",
                        color = Color.Cyan,
                        fontSize = 12.sp
                    )
                }
            }

            if (tab == LeaderboardTab.PVP) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${entry.pvpWins} Wins",
                        color = Color.Green,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${entry.pvpLosses} Losses",
                        color = Color.Red,
                        fontSize = 10.sp
                    )
                }
            } else {
                Text(
                    text = "LEVEL ${entry.level}",
                    color = Color.Yellow,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
