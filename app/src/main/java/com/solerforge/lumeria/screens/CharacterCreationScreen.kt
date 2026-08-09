package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.RpgButton

@Composable
fun CharacterCreationScreen(
    title: String = "FORGE A NEW LEGACY",
    buttonText: String = "BEGIN JOURNEY",
    nameAvailability: Boolean?,
    onCheckName: (String) -> Unit,
    onConfirm: (name: String, gender: String, className: String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var selectedClass by remember { mutableStateOf("Warrior") }

    LaunchedEffect(name) {
        if (name.length >= 3) {
            onCheckName(name)
        }
    }
    
    val classes = listOf(
        Triple("Warrior", "High Strength & Vitality. A master of the blade.", Color(0xFFD32F2F)),
        Triple("Mage", "High Intelligence & Mana. Wielder of arcane arts.", Color(0xFF1976D2)),
        Triple("Samurai", "High Agility & Luck. A swift and precise fighter.", Color(0xFF388E3C)),
        Triple("Paladin", "Ultimate Defense & Holy Power. An unbreakable wall.", Color(0xFFFBC02D)),
        Triple("Assassin", "Extreme Luck & Agility. Strikes from the shadows.", Color(0xFF7B1FA2)),
        Triple("Monk", "High Vitality & Wisdom. Uses spirit and fists.", Color(0xFFE64A19)),
        Triple("Archer", "Balanced Agility & Strength. Master of long range.", Color(0xFF689F38)),
        Triple("Necromancer", "High Mana & Dark Power. Commands the void.", Color(0xFF424242)),
        Triple("Bard", "High Luck & Wisdom. Inspires through music.", Color(0xFFF06292)),
        Triple("Berserker", "Pure Strength & Speed. Reckless destruction.", Color(0xFFB71C1C))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Yellow,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // NAME ENTRY
            Text("HERO NAME", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            TextField(
                value = name,
                onValueChange = { if (it.length <= 12) name = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("Enter name...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.DarkGray,
                    unfocusedContainerColor = Color.Black.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (name.length >= 3) {
                Text(
                    text = when (nameAvailability) {
                        true -> "Name Available"
                        false -> "Name Already Taken"
                        null -> "Checking..."
                    },
                    color = when (nameAvailability) {
                        true -> Color.Green
                        false -> Color.Red
                        null -> Color.Gray
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GENDER SELECTION
            Text("GENDER", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Male", "Female").forEach { g ->
                    Button(
                        onClick = { gender = g },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gender == g) Color.Cyan else Color.DarkGray
                        )
                    ) {
                        Text(g, color = if (gender == g) Color.Black else Color.White, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CLASS SELECTION
            Text("STARTING CLASS", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                classes.forEach { (className, desc, color) ->
                    ClassCard(
                        name = className,
                        description = desc,
                        isSelected = selectedClass == className,
                        accentColor = color,
                        onClick = { selectedClass = className }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            RpgButton(
                text = buttonText,
                enabled = name.isNotBlank() && nameAvailability == true,
                onClick = { onConfirm(name, gender, selectedClass) },
                containerColor = Color(0xFF6200EE),
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )

            Text(
                text = "Cancel",
                color = Color.Gray,
                modifier = Modifier.clickable { onCancel() }.padding(16.dp)
            )
        }
    }
}

@Composable
fun ClassCard(
    name: String,
    description: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f))
            .border(2.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) accentColor else Color.White)
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
        }
    }
}
