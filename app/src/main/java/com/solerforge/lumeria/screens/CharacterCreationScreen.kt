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
import com.solerforge.lumeria.database.ClassDatabase
import com.solerforge.lumeria.database.RaceDatabase

@Composable
fun CharacterCreationScreen(
    title: String = "FORGE A NEW LEGACY",
    buttonText: String = "BEGIN JOURNEY",
    nameAvailability: Boolean?,
    isIdentityReady: Boolean = true,
    rebirthError: String? = null,
    onClearError: () -> Unit = {},
    onCheckName: (String) -> Unit,
    onConfirm: (name: String, gender: String, className: String, raceName: String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var selectedClass by remember { mutableStateOf("Warrior") }
    
    var selectedRace by remember { mutableStateOf(RaceDatabase.getRace("Human")) }
    var isRolling by remember { mutableStateOf(false) }
    var showMonsterWarning by remember { mutableStateOf(false) }

    val classes = remember(selectedRace) { ClassDatabase.getAvailableClasses(selectedRace.name) }
    
    LaunchedEffect(selectedRace) {
        if (classes.none { it.name == selectedClass }) {
            selectedClass = classes.first().name
        }
    }

    LaunchedEffect(name) {
        if (name.length >= 3) {
            onCheckName(name)
        }
    }
    
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
                    text = when {
                        name.lowercase().trim() == "xious" -> "Name 'Xious' is reserved for the Legend."
                        nameAvailability == true -> "Name Available"
                        nameAvailability == false -> "Name Already Taken"
                        else -> "Checking Name Availability..."
                    },
                    color = when {
                        name.lowercase().trim() == "xious" || nameAvailability == false -> Color.Red
                        nameAvailability == true -> Color.Green
                        else -> Color.Gray
                    },
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    textAlign = TextAlign.Start
                )
                
                if (nameAvailability == null && name.length >= 3) {
                     Text(
                        text = "If this persists, check your internet connection.",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
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

            // RACE SELECTION (ROLL BASED)
            Text("HERO RACE", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(1.dp, selectedRace.rarity.color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedRace.name.uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = selectedRace.rarity.color,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "[${selectedRace.rarity.label}]",
                        style = MaterialTheme.typography.labelSmall,
                        color = selectedRace.rarity.color.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedRace.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )
                    
                    val bonuses = buildString {
                        if (selectedRace.strBonus > 0) append("+${selectedRace.strBonus} STR ")
                        if (selectedRace.vitBonus > 0) append("+${selectedRace.vitBonus} VIT ")
                        if (selectedRace.defBonus > 0) append("+${selectedRace.defBonus} DEF ")
                        if (selectedRace.intBonus > 0) append("+${selectedRace.intBonus} INT ")
                        if (selectedRace.agiBonus > 0) append("+${selectedRace.agiBonus} AGI ")
                        if (selectedRace.luckBonus > 0) append("+${selectedRace.luckBonus} LCK ")
                        if (selectedRace.wisBonus > 0) append("+${selectedRace.wisBonus} WIS ")
                    }.trim()
                    
                    val debuffs = buildString {
                        if (selectedRace.strBonus < 0) append("${selectedRace.strBonus} STR ")
                        if (selectedRace.vitBonus < 0) append("${selectedRace.vitBonus} VIT ")
                        if (selectedRace.defBonus < 0) append("${selectedRace.defBonus} DEF ")
                        if (selectedRace.intBonus < 0) append("${selectedRace.intBonus} INT ")
                        if (selectedRace.agiBonus < 0) append("${selectedRace.agiBonus} AGI ")
                        if (selectedRace.luckBonus < 0) append("${selectedRace.luckBonus} LCK ")
                        if (selectedRace.wisBonus < 0) append("${selectedRace.wisBonus} WIS ")
                    }.trim()
                    
                    if (bonuses.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bonuses: $bonuses",
                            color = Color.Green,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (debuffs.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Debuffs: $debuffs",
                            color = Color.Red,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    RpgButton(
                        text = if (isRolling) "ROLLING..." else "ROLL NEW RACE",
                        onClick = {
                            isRolling = true
                            selectedRace = RaceDatabase.rollRace()
                            isRolling = false
                        },
                        enabled = !isRolling,
                        modifier = Modifier.height(40.dp).width(200.dp),
                        containerColor = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CLASS SELECTION
            Text("STARTING CLASS", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                classes.forEach { pc ->
                    ClassCard(
                        name = pc.name,
                        description = pc.description,
                        isSelected = selectedClass == pc.name,
                        accentColor = pc.color,
                        onClick = { selectedClass = pc.name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            RpgButton(
                text = buttonText,
                enabled = name.length in 3..12 && nameAvailability == true && isIdentityReady && !isRolling,
                onClick = { 
                    if (selectedRace.isMonster) {
                        showMonsterWarning = true
                    } else {
                        onConfirm(name, gender, selectedClass, selectedRace.name)
                    }
                },
                containerColor = Color(0xFF6200EE),
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )

            Text(
                text = "Cancel",
                color = Color.Gray,
                modifier = Modifier.clickable { onCancel() }.padding(16.dp)
            )
        }

        rebirthError?.let {
            AlertDialog(
                onDismissRequest = onClearError,
                title = { Text("Rebirth Error") },
                text = { Text(it) },
                confirmButton = {
                    Button(onClick = onClearError) { Text("OK") }
                }
            )
        }

        if (showMonsterWarning) {
            AlertDialog(
                onDismissRequest = { showMonsterWarning = false },
                title = { Text("MONSTER RACE WARNING", color = Color.Red, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("You are about to rebirth as a ${selectedRace.name}.", fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Monsters face significant restrictions compared to Heroes:", color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Cannot roll for permanent traits.", color = Color.Yellow)
                        if (!selectedRace.canWearGear) {
                            Text("• Cannot equip standard human weapons or armor.", color = Color.Yellow)
                        }
                        if (!selectedRace.canHavePets) {
                            Text("• Cannot have pets.", color = Color.Yellow)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("This path is intended for experienced players seeking a unique challenge. Are you sure you want to proceed?", color = Color.White)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showMonsterWarning = false
                            onConfirm(name, gender, selectedClass, selectedRace.name)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("CONFIRM REBIRTH", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showMonsterWarning = false }) {
                        Text("GO BACK", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1A1A1A),
                textContentColor = Color.White
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
