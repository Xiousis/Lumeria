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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.data.PlayerData

enum class GamblingStatus {
    BETTING, PLAYER_TURN, DEALER_TURN, ENDED
}

data class Card(val rank: String, val suit: String, val value: Int)

fun createDeck(): MutableList<Card> {
    val ranks = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")
    val suits = listOf("♠", "♥", "♦", "♣")
    val deck = mutableListOf<Card>()
    for (suit in suits) {
        for (rank in ranks) {
            val value = when (rank) {
                "J", "Q", "K" -> 10
                "A" -> 11
                else -> rank.toInt()
            }
            deck.add(Card(rank, suit, value))
        }
    }
    deck.shuffle()
    return deck
}

fun calculateHandValue(hand: List<Card>): Int {
    var value = hand.sumOf { it.value }
    var aces = hand.count { it.rank == "A" }
    while (value > 21 && aces > 0) {
        value -= 10
        aces -= 1
    }
    return value
}

@Composable
fun GamblingHouseScreen(
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var status by remember { mutableStateOf(GamblingStatus.BETTING) }
    var deck by remember { mutableStateOf(createDeck()) }
    var playerHand by remember { mutableStateOf(listOf<Card>()) }
    var dealerHand by remember { mutableStateOf(listOf<Card>()) }
    var currentBet by remember { mutableIntStateOf(10) }
    var frankDialogue by remember { mutableStateOf("Welcome to my house! Ready to place a bet?") }

    LaunchedEffect(Unit) {
        com.example.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    val playerValue = calculateHandValue(playerHand)
    val dealerValue = calculateHandValue(dealerHand)

    fun startNewGame() {
        if (playerData.gold < currentBet) {
            frankDialogue = "You're a bit short on gold there, friend."
            return
        }
        
        deck = createDeck()
        playerHand = listOf(deck.removeAt(0), deck.removeAt(0))
        dealerHand = listOf(deck.removeAt(0), deck.removeAt(0))
        
        val initialPlayerValue = calculateHandValue(playerHand)
        if (initialPlayerValue == 21) {
            val payout = (currentBet * 1.5).toInt()
            onPlayerUpdate(playerData.copy(gold = playerData.gold + payout, gamblingWins = playerData.gamblingWins + 1))
            frankDialogue = "BLACKJACK! Unbelievable luck! You win ${payout} gold!"
            status = GamblingStatus.ENDED
        } else {
            onPlayerUpdate(playerData.copy(gold = playerData.gold - currentBet))
            status = GamblingStatus.PLAYER_TURN
            frankDialogue = "Cards are on the table. What's your move?"
        }
    }

    fun hit() {
        playerHand = playerHand + deck.removeAt(0)
        if (calculateHandValue(playerHand) > 21) {
            frankDialogue = "Bust! Better luck next time. You lose $currentBet gold."
            status = GamblingStatus.ENDED
        } else {
            frankDialogue = "Another one? Feeling lucky?"
        }
    }

    fun stand() {
        frankDialogue = "Staying, eh? Let's see what I've got..."
        status = GamblingStatus.DEALER_TURN
    }

    LaunchedEffect(status) {
        if (status == GamblingStatus.DEALER_TURN) {
            var currentDealerHand = dealerHand
            while (calculateHandValue(currentDealerHand) < 17) {
                kotlinx.coroutines.delay(800)
                currentDealerHand = currentDealerHand + deck.removeAt(0)
                dealerHand = currentDealerHand
            }
            
            val finalDealerValue = calculateHandValue(dealerHand)
            val finalPlayerValue = calculateHandValue(playerHand)
            
            if (finalDealerValue > 21) {
                frankDialogue = "I busted! You win $currentBet gold!"
                onPlayerUpdate(playerData.copy(gold = playerData.gold + (currentBet * 2), gamblingWins = playerData.gamblingWins + 1))
            } else if (finalDealerValue > finalPlayerValue) {
                frankDialogue = "Looks like I take this one. $currentBet gold is mine."
            } else if (finalDealerValue < finalPlayerValue) {
                frankDialogue = "You beat me! Take your $currentBet gold."
                onPlayerUpdate(playerData.copy(gold = playerData.gold + (currentBet * 2), gamblingWins = playerData.gamblingWins + 1))
            } else {
                frankDialogue = "A tie! A push! Your bet is safe."
                onPlayerUpdate(playerData.copy(gold = playerData.gold + currentBet))
            }
            status = GamblingStatus.ENDED
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.frank),
            contentDescription = "Gambling House Background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = 1.1f, scaleY = 1.1f),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        // Overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color(0xFF003300).copy(alpha = 0.8f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "THE GAMBLING HOUSE",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gold: ${playerData.gold}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFFD600)
                    )
                }
                
                // Frank's Portrait
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Cyan, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.frank),
                        contentDescription = "Frank NPC",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Dealer Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Dealer (Frank's Hand)", color = Color.LightGray, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dealerHand.size) { index ->
                        val card = dealerHand[index]
                        val hideCard = status == GamblingStatus.PLAYER_TURN && index == 1
                        CardView(card = card, isHidden = hideCard)
                    }
                }
                if (status != GamblingStatus.PLAYER_TURN && dealerHand.isNotEmpty()) {
                    Text("Value: $dealerValue", color = Color.White, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // Frank's Dialogue Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FRANK",
                        color = Color.Cyan,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$frankDialogue\"",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.wrapContentHeight()
                    )
                }
            }

            // Player Section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Your Hand", color = Color.LightGray, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(playerHand) { card ->
                        CardView(card = card)
                    }
                }
                if (playerHand.isNotEmpty()) {
                    Text("Value: $playerValue", color = Color.White, modifier = Modifier.padding(top = 4.dp))
                }
            }

            // Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (status) {
                    GamblingStatus.BETTING, GamblingStatus.ENDED -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(onClick = { if (currentBet >= 10) currentBet -= 10 }) {
                                Text("-10", color = Color.White)
                            }
                            Text(
                                "Bet: $currentBet",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            IconButton(onClick = { if (playerData.gold >= currentBet + 10) currentBet += 10 }) {
                                Text("+10", color = Color.White)
                            }
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val buttonAsset = if (status == GamblingStatus.BETTING) R.drawable.deal_button_font else R.drawable.play_again_button_font
                            val canAfford = playerData.gold >= currentBet
                            
                            Image(
                                painter = painterResource(id = buttonAsset),
                                contentDescription = if (status == GamblingStatus.BETTING) "Deal" else "Play Again",
                                modifier = Modifier
                                    .height(80.dp)
                                    .clickable(enabled = canAfford) { startNewGame() },
                                contentScale = ContentScale.Fit,
                                alpha = if (canAfford) 1f else 0.4f
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
                    GamblingStatus.PLAYER_TURN -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hit_button_font),
                                contentDescription = "Hit",
                                modifier = Modifier
                                    .height(80.dp)
                                    .clickable { hit() },
                                contentScale = ContentScale.Fit
                            )
                            
                            Image(
                                painter = painterResource(id = R.drawable.stand_button_font),
                                contentDescription = "Stand",
                                modifier = Modifier
                                    .height(80.dp)
                                    .clickable { stand() },
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    GamblingStatus.DEALER_TURN -> {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CardView(card: Card, isHidden: Boolean = false) {
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isHidden) Color(0xFFBDBDBD) else Color.White)
            .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!isHidden) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = card.rank,
                    color = if (card.suit == "♥" || card.suit == "♦") Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = card.suit,
                    color = if (card.suit == "♥" || card.suit == "♦") Color.Red else Color.Black,
                    fontSize = 24.sp
                )
            }
        } else {
            Text("?", color = Color.DarkGray, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        }
    }
}
