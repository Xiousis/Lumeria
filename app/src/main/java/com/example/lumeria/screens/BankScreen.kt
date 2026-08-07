package com.example.lumeria.screens

import com.example.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.example.lumeria.components.RpgButton
import com.example.lumeria.data.PlayerData
import com.example.lumeria.utils.CurrencyUtils

@Composable
fun BankScreen(
    playerData: PlayerData,
    onDeposit: (Int) -> Unit,
    onWithdraw: (Int) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var message by remember { mutableStateOf("Welcome to the Vault! Safe storage for your hard-earned gold.") }

    LaunchedEffect(Unit) {
        com.example.lumeria.utils.MusicManager.playMusic(context, R.raw.bank_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bank_screen_bg),
            contentDescription = "Bank Background",
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Ironclad Bank",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Black
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("In Wallet", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Text(
                            CurrencyUtils.formatGold(playerData.gold),
                            color = Color.Yellow,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("In Bank", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                        Text(
                            CurrencyUtils.formatGold(playerData.bankGold),
                            color = Color(0xFF4ADE80),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Transactions", color = Color.LightGray, style = MaterialTheme.typography.labelLarge)
            
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BankActionButton("Deposit 100", playerData.gold >= 100, Modifier.weight(1f)) {
                    onDeposit(100)
                    message = "Safe and sound!"
                }
                BankActionButton("Deposit 1k", playerData.gold >= 1000, Modifier.weight(1f)) {
                    onDeposit(1000)
                    message = "The vault grows stronger!"
                }
                BankActionButton("All", playerData.gold > 0, Modifier.weight(1f)) {
                    onDeposit(playerData.gold)
                    message = "Everything's locked up tight!"
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BankActionButton("Withdraw 100", playerData.bankGold >= 100, Modifier.weight(1f), Color(0xFF94A3B8)) {
                    onWithdraw(100)
                    message = "Don't spend it all in one place!"
                }
                BankActionButton("Withdraw 1k", playerData.bankGold >= 1000, Modifier.weight(1f), Color(0xFF94A3B8)) {
                    onWithdraw(1000)
                    message = "Be careful out there."
                }
                BankActionButton("All", playerData.bankGold > 0, Modifier.weight(1f), Color(0xFF94A3B8)) {
                    onWithdraw(playerData.bankGold)
                    message = "Emptying the vault? Brave soul."
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Gold in the bank is protected from defeat penalties!",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

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
fun BankActionButton(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.Transparent,
    onClick: () -> Unit
) {
    RpgButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp)
    )
}
