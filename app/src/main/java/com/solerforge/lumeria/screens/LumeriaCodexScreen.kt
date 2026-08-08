package com.solerforge.lumeria.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.R

data class CodexEntry(
    val title: String,
    val description: String,
    val icon: Int? = null
)

data class CodexCategory(
    val name: String,
    val entries: List<CodexEntry>
)

@Composable
fun LumeriaCodexScreen(onReturn: () -> Unit) {
    val categories = listOf(
        CodexCategory(
            "Attributes",
            listOf(
                CodexEntry("Strength (STR)", "Increases physical damage dealt by 2 per point. Higher strength allows you to crush enemies with brute force."),
                CodexEntry("Vitality (VIT)", "Each point grants +10 Max HP. High vitality is essential for surviving the devastating 'Warrior Arts' used by bosses."),
                CodexEntry("Defense (DEF)", "Reduces incoming damage from all sources. Higher defense makes you harder to take down."),
                CodexEntry("Intelligence (INT)", "Increases magic damage and grants +5 Max Mana per point. Crucial for mages using high-tier elemental skills."),
                CodexEntry("Agility (AGI)", "Grants +2% Critical Hit chance and +1% Dodge chance per point. Allows you to strike fast and avoid danger."),
                CodexEntry("Luck (LCK)", "A versatile stat. Grants +0.5% Crit, +0.2% Dodge, and +1% Gold found per point. Higher luck also helps in the Gambling House."),
                CodexEntry("Wisdom (WIS)", "Determines your Mana regeneration per turn. You regain 1 Mana for every 5 points of Wisdom. Vital for prolonged battles.")
            )
        ),
        CodexCategory(
            "Combat Systems",
            listOf(
                CodexEntry("Combos", "Using different skills in sequence can trigger combos for massive bonus damage. Experiment with attack patterns!"),
                CodexEntry("Warrior Arts", "Powerful techniques used by Arena Champions and World Bosses. They often have high multipliers or ignore armor entirely."),
                CodexEntry("Status Effects", "Many skills apply effects like Burn (damage over time), Stun (skips turn), or Bleed (heavy DOT). Use them to your advantage."),
                CodexEntry("Elemental Damage", "Fire, Ice, and Lightning deal bonus damage to enemies weak to those elements. Check the Bestiary for enemy weaknesses.")
            )
        ),
        CodexCategory(
            "World & Economy",
            listOf(
                CodexEntry("Kingdom Laws", "Proclaimed by the King, these laws apply realm-wide effects. Some increase loot drops while others make enemies more dangerous."),
                CodexEntry("Nation Upgrades", "Investing in Lumeria at the Royal Court grants permanent discounts in Billy's Shop and improved resource gathering."),
                CodexEntry("Banking", "Gold kept in the Bank is safe from the 15% penalty incurred upon defeat. Always deposit your surplus!")
            )
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bestiary_screen_bg),
            contentDescription = "Codex Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Lumeria Codex",
                style = MaterialTheme.typography.displaySmall,
                color = Color.Yellow,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                categories.forEach { category ->
                    item {
                        Text(
                            text = category.name,
                            color = Color.Cyan,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(category.entries) { entry ->
                        CodexEntryCard(entry)
                    }
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

@Composable
fun CodexEntryCard(entry: CodexEntry) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = entry.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = entry.description,
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}
