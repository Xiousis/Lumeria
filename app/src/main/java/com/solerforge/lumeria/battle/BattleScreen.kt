package com.solerforge.lumeria.battle

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.solerforge.lumeria.R
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.utils.MusicManager
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FloatingNumberItem(num: FloatingNumber) {
    // Using a key to trigger animation once
    var startAnim by remember { mutableStateOf(value = false) }
    
    val shake by animateFloatAsState(
        targetValue = if (num.damageType == DamageType.Crit && startAnim) 10f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Shake"
    )

    val animatedY by animateFloatAsState(
        targetValue = if (startAnim) {
            if (num.damageType == DamageType.Magic) num.initialY - 180f else num.initialY - 120f
        } else num.initialY,
        animationSpec = tween(if (num.damageType == DamageType.Heal) 800 else 1200, easing = LinearOutSlowInEasing),
        label = "Y",
    )
    
    val animatedX by animateFloatAsState(
        targetValue = if (num.damageType == DamageType.Magic && startAnim) num.initialX + 40f else num.initialX,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "X"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (startAnim) 0f else 1f,
        animationSpec = tween(1200, easing = LinearEasing),
        label = "A"
    )

    LaunchedEffect(Unit) {
        startAnim = true
    }

    val isCrit = num.damageType == DamageType.Crit
    Text(
        text = if (isCrit) stringResource(R.string.battle_crit_format, num.text) else num.text,
        color = when(num.damageType) {
            DamageType.Magic -> Color(0xFFA855F7)
            DamageType.Status -> Color.Yellow
            else -> num.color
        },
        fontSize = when(num.damageType) {
            DamageType.Crit -> 34.sp
            DamageType.Magic -> 24.sp
            DamageType.Heal -> 26.sp
            else -> 22.sp
        },
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .offset(x = (animatedX + shake).dp, y = animatedY.dp)
            .graphicsLayer { 
                this.alpha = animatedAlpha
                if (isCrit) {
                    this.scaleX = 1.2f
                    this.scaleY = 1.2f
                }
            }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BattleScreen(
    modifier: Modifier = Modifier,
    viewModel: BattleViewModel,
    onDeath: (PlayerData) -> Unit,
    onPlayerUpdate: (PlayerData) -> Unit,
    onBattleAgain: (PlayerData) -> Unit,
    onLeave: (PlayerData) -> Unit,
    onFlee: (PlayerData) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsState()
    val enemy = state.enemy
    val playerData = state.currentPlayerSnapshot
    
    val equippedWeapon = remember(playerData.equippedWeapon) { GameDatabase.getWeapon(playerData.equippedWeapon) }
    val equippedArmor = remember(playerData.equippedArmor) { GameDatabase.getArmor(playerData.equippedArmor) }
    
    val deathAlpha by animateFloatAsState(
        targetValue = if (state.isDying) 1f else 0f,
        animationSpec = tween(1000),
        label = "DeathAlpha",
    )

    val pOffset by animateFloatAsState(targetValue = state.playerOffsetX, animationSpec = tween(100), label = "PlayerOffset")
    val eOffset by animateFloatAsState(targetValue = state.enemyOffsetX, animationSpec = tween(100), label = "EnemyOffset")

    var showItemMenu by remember { mutableStateOf(value = false) }
    var tooltipSkill by remember { mutableStateOf<String?>(null) }
    
    val particles = remember { mutableStateListOf<BattleParticle>() }
    
    LaunchedEffect(state.locationName) {
        MusicManager.playMusic(context, getBattleMusic(state.locationName, state.isBossBattle, state.towerFloor))
    }

    LaunchedEffect(state.enemyFlashAlpha) {
        if (state.enemyFlashAlpha > 0.5f) {
            repeat(15) {
                particles.add(
                    BattleParticle(
                        x = 300f + (Math.random() * 50).toFloat(),
                        y = 200f + (Math.random() * 50).toFloat(),
                        vx = ((Math.random() - 0.5) * 10).toFloat(),
                        vy = ((Math.random() - 0.5) * 10).toFloat(),
                        color = Color.Red, // Simplified particle color
                        life = 1.0f,
                        size = (5..15).random().toFloat(),
                    ),
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        while(true) {
            withFrameMillis { 
                particles.forEach { p ->
                    p.x += p.vx
                    p.y += p.vy
                    p.life -= 0.05f
                }
                particles.removeAll { it.life <= 0 }
            }
        }
    }

    LaunchedEffect(state.isDying) {
        if (state.isDying) {
            delay(1000.milliseconds)
            onDeath(state.currentPlayerSnapshot)
        }
    }

    LaunchedEffect(state.victoryProcessed) {
        if (state.victoryProcessed) {
            showItemMenu = false
            tooltipSkill = null
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = getBattleBackground(state.locationName, state.isBossBattle, state.towerFloor)),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f, // Increased visibility of detailed backgrounds
        )
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.life),
                    radius = p.size * p.life,
                    center = androidx.compose.ui.geometry.Offset(p.x, p.y),
                )
            }
        }

        // DAMAGE NUMBERS LAYER
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            state.floatingNumbers.forEach { num ->
                FloatingNumberItem(num)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .graphicsLayer {
                    translationX = state.screenShake
                    translationY = state.screenShake * 0.5f
                }
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    UnitCard(
                        name = if (playerData.isReborn) playerData.playerName else stringResource(R.string.battle_hero_name),
                        level = playerData.level,
                        hp = state.playerHp,
                        maxHp = playerData.maxHp,
                        mana = state.playerMana,
                        maxMana = playerData.maxMana,
                        gold = playerData.gold,
                        portraitId = if (playerData.isReborn) 0 else (if (playerData.unlockedTraits.contains("Eyes of the Creator")) R.drawable.xious_battle_eyes else R.drawable.battle_xious),
                        rebornRace = if (playerData.isReborn) playerData.playerRace else null,
                        offsetX = pOffset,
                        shake = state.playerShake,
                        flashAlpha = state.playerFlashAlpha,
                        isHero = true,
                        isBoss = false,
                        isActive = !state.isProcessing && !state.victoryProcessed && !state.isDying,
                        activeBuffs = playerData.activeBuffs,
                        showNumbers = true,
                        statusIcons = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (state.playerStunnedTurns > 0) StatusBadge("💫", Color.Yellow)
                                if (state.playerPoisonTurns > 0) StatusBadge("🤢", Color(0xFF4CAF50))
                                if ((state.attackBuffTurns > 0) || (state.superBuffTurns > 0)) StatusBadge("🔥", Color.Red)
                                if (state.critBuffTurns > 0) StatusBadge("🎯", Color.Cyan)
                                if (state.parryActive) StatusBadge("🛡️", Color.White)
                            }
                        },
                        footer = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.battle_stats_format, equippedWeapon.attack, equippedArmor.defense),
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    // FAMILIAR ICON
                    if (playerData.equippedFamiliar != "None") {
                        val familiarLogoId = viewModel.getFamiliarLogoId(playerData.equippedFamiliar)
                        if (familiarLogoId != 0) {
                            Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                Image(
                                    painter = painterResource(id = familiarLogoId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .border(2.dp, Color.Magenta, androidx.compose.foundation.shape.CircleShape)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .padding(4.dp),
                                    contentScale = ContentScale.Fit,
                                )
                                
                                // Action Counter Badge
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.BottomEnd)
                                        .background(Color.Magenta, androidx.compose.foundation.shape.CircleShape)
                                        .border(1.dp, Color.White, androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    val familiar = com.solerforge.lumeria.database.FamiliarDatabase.getFamiliar(playerData.equippedFamiliar)
                                    Text(
                                        text = (familiar.supportInterval - state.familiarActionCounter).toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                UnitCard(
                    name = enemy.name,
                    level = enemy.level,
                    hp = state.enemyHp,
                    maxHp = enemy.maxHp,
                    portraitId = state.enemyLogoId,
                    offsetX = eOffset,
                    shake = state.enemyShake,
                    flashAlpha = state.enemyFlashAlpha,
                    isHero = false,
                    isBoss = viewModel.isBossBattle,
                    isActive = (state.isProcessing) && (state.enemyHp > 0),
                    showNumbers = (playerData.killCounts[enemy.name] ?: 0) >= 50,
                    statusIcons = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (state.enemyStunnedTurns > 0) StatusBadge("💫", Color.Yellow)
                            if (state.bleedTurns > 0) StatusBadge("🩸", Color.Red)
                            if (state.burnTurns > 0) StatusBadge("🔥", Color(0xFFFF5722))
                            if (state.poisonTurns > 0) StatusBadge("🤢", Color(0xFF4CAF50))
                            if ((state.bossShieldTurns > 0) && (viewModel.isBossBattle)) StatusBadge("🛡️", Color.White)
                        }
                    },
                    footer = {
                        Row(
                            modifier = Modifier.padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            if ((playerData.killCounts[enemy.name] ?: 0) >= 150) {
                                enemy.elementalResistances.forEach { (type, mult) ->
                                    ElementBadge(type, mult)
                                }
                            } else {
                                Text("???", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
                
                val enemy2 = state.enemy2
                if (enemy2 != null) {
                    val e2Offset by animateFloatAsState(targetValue = state.enemy2OffsetX, animationSpec = tween(100), label = "Enemy2Offset")
                    UnitCard(
                        name = enemy2.name,
                        level = enemy2.level,
                        hp = state.enemy2Hp,
                        maxHp = enemy2.maxHp,
                        portraitId = state.enemy2LogoId,
                        offsetX = e2Offset,
                        shake = state.enemy2Shake,
                        flashAlpha = state.enemy2FlashAlpha,
                        isHero = false,
                        isBoss = false,
                        isActive = (state.isProcessing) && (state.enemy2Hp > 0),
                        showNumbers = (playerData.killCounts[enemy2.name] ?: 0) >= 50,
                        statusIcons = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (state.enemy2StunnedTurns > 0) StatusBadge("💫", Color.Yellow)
                                if (state.enemy2BleedTurns > 0) StatusBadge("🩸", Color.Red)
                                if (state.enemy2BurnTurns > 0) StatusBadge("🔥", Color(0xFFFF5722))
                                if (state.enemy2PoisonTurns > 0) StatusBadge("🤢", Color(0xFF4CAF50))
                            }
                        },
                        footer = {
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                if ((playerData.killCounts[enemy2.name] ?: 0) >= 150) {
                                    enemy2.elementalResistances.forEach { (type, mult) ->
                                        ElementBadge(type, mult)
                                    }
                                } else {
                                    Text("???", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // TURN INDICATOR
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                val turnText = when {
                    state.victoryProcessed -> stringResource(R.string.battle_victory_indicator)
                    state.isDying -> stringResource(R.string.battle_defeat_indicator)
                    state.isProcessing -> stringResource(R.string.battle_processing_indicator)
                    else -> stringResource(R.string.battle_your_turn_indicator)
                }
                val turnColor = when {
                    state.victoryProcessed -> Color.Green
                    state.isDying -> Color.Red
                    state.isProcessing -> Color.LightGray
                    else -> Color.Yellow
                }
                
                Text(
                    text = turnText,
                    color = turnColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ACTION GRID
            val isStunned = state.playerStunnedTurns > 0
            val isVictory = state.victoryProcessed
            
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val skillRows = playerData.equippedSkills.asSequence().take(4).chunked(2).toList()
                val allSkillsOnCooldown = playerData.equippedSkills.all { name ->
                    val s = com.solerforge.lumeria.database.SkillDatabase.getSkill(name)
                    ((state.skillCooldowns[name] ?: 0) > 0) || (state.playerMana < s.manaCost)
                }

                if (isStunned && !isVictory && !state.isProcessing) {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        onClick = { viewModel.takeTurn("None", onPlayerUpdate) }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.battle_stunned_status), fontWeight = FontWeight.Bold, color = Color.White)
                            Text(stringResource(R.string.battle_tap_to_recover), fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                } else if (allSkillsOnCooldown && !isVictory && !state.isProcessing) {
                    Button(
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64)),
                        onClick = { viewModel.takeTurn("None", onPlayerUpdate) }
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.battle_no_moves), fontWeight = FontWeight.Bold, color = Color.White)
                            Text(stringResource(R.string.battle_tap_to_rest), fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                } else {
                    skillRows.forEach { rowSkills ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            rowSkills.forEach { skillName ->
                                val skill = com.solerforge.lumeria.database.SkillDatabase.getSkill(skillName)
                                if (skill.name != "None") {
                                    val cooldown = state.skillCooldowns[skillName] ?: 0
                                    val canAfford = state.playerMana >= skill.manaCost
                                    val isReady = (cooldown == 0) && (!isStunned) && (!state.isProcessing)
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (!isVictory && canAfford && isReady) {
                                                    when (skill.skillType) {
                                                        "Attack" -> Color(0xFF8B0000)
                                                        "Support" -> Color(0xFF006400)
                                                        "Buff" -> Color(0xFF8B8B00)
                                                        else -> MaterialTheme.colorScheme.primary
                                                    }
                                                } else Color.DarkGray,
                                            )
                                            .border(
                                                1.dp,
                                                if (isReady && canAfford) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                                                RoundedCornerShape(8.dp),
                                            )
                                            .combinedClickable(
                                                enabled = (!isVictory) && canAfford && isReady && (!state.isDying) && ((state.enemyHp > 0) || (state.enemy2Hp > 0)),
                                                onClick = { 
                                                    viewModel.takeTurn(skillName, onPlayerUpdate)
                                                },
                                                onLongClick = { tooltipSkill = skillName },
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = skill.name, 
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (canAfford) Color.White else Color.Red.copy(alpha = 0.7f),
                                                fontWeight = FontWeight.Bold,
                                            )
                                            if (cooldown > 0) {
                                                Text(
                                                    text = stringResource(R.string.battle_cooldown_format, cooldown),
                                                    color = Color.Yellow,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                )
                                            } else {
                                                Text(
                                                    text = stringResource(R.string.battle_mana_cost_format, skill.manaCost),
                                                    color = if (canAfford) Color.Cyan else Color.Red,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                )
                                            }
                                        }
                                        
                                        // Cooldown overlay
                                        if (cooldown > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Black.copy(alpha = 0.4f)),
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        modifier = Modifier.weight(1f).height(48.dp),
                        enabled = !isVictory && !isStunned && !state.isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        onClick = { showItemMenu = true },
                    ) {
                        Text(stringResource(R.string.battle_action_items))
                    }
                    Button(
                        modifier = Modifier.weight(1f).height(48.dp),
                        enabled = !isVictory && !isStunned && state.isFleeable && !state.isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        onClick = { onFlee(playerData.copy(hp = state.playerHp, mana = state.playerMana)) },
                    ) {
                        Text(stringResource(R.string.battle_action_run))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BattleLog(
                logEntries = state.battleLog,
                message = state.battleMessage,
                formatLog = { formatBattleLog(it) },
                modifier = Modifier.weight(1f).fillMaxWidth(),
            )
        }

        // ITEM MENU OVERLAY
        if (showItemMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showItemMenu = false },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color.DarkGray, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .clickable(enabled = false) {},
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val race = com.solerforge.lumeria.database.RaceDatabase.getRace(playerData.playerRace)
                    
                    Text(stringResource(R.string.battle_items_title), color = Color.Cyan, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    val consumables = playerData.inventory.asSequence().filter { 
                        it.contains("Potion") || it.contains("Elixir") || it.contains("Vial") 
                    }.groupingBy { it }.eachCount()

                    if (!race.canUsePotions) {
                         Text(
                            text = "Monsters cannot use human consumables. You rely on your natural strength.",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else if (consumables.isEmpty()) {
                        Text(stringResource(R.string.battle_no_items), color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(consumables.toList()) { (itemName, count) ->
                                Button(
                                    onClick = {
                                        viewModel.useItem(itemName, onPlayerUpdate)
                                        showItemMenu = false
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(itemName, color = Color.White)
                                        Text("x$count", color = Color.Yellow)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { showItemMenu = false }) {
                        Text(stringResource(R.string.battle_close))
                    }
                }
            }
        }

        if (deathAlpha > 0f) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = deathAlpha)))
        }

        if (state.victoryProcessed) {
             Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color.Black, RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Yellow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.battle_victory_title), style = MaterialTheme.typography.headlineMedium, color = Color.Yellow)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.battle_victory_rewards, state.xpGained, CurrencyUtils.formatGold(state.goldGained)), color = Color.Green)
                    
                    if (state.victoryLoot.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.battle_loot_header), color = Color.LightGray)
                        state.victoryLoot.forEach { Text(it, color = Color.White) }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (!state.isStoryMode) {
                            Button(onClick = { onBattleAgain(playerData) }) { Text(stringResource(R.string.battle_btn_again)) }
                        }
                        Button(onClick = { onLeave(playerData) }) { 
                            Text(if (state.isStoryMode) stringResource(R.string.battle_btn_continue) else stringResource(R.string.battle_btn_return)) 
                        }
                    }
                }
            }
        }

        // LOW HP WARNING
        if (state.isLowHpWarning && !state.victoryProcessed && !state.isDying) {
            val infiniteTransition = rememberInfiniteTransition(label = "LowHP")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0.4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "Alpha"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(8.dp, Color.Red.copy(alpha = alpha))
            )
        }

        // SKILL EVOLUTION BANNER
        state.evolvedSkillName?.let { evolvedName ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { viewModel.clearEvolutionBanner() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color.DarkGray, RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Cyan, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SKILL EVOLVED!", color = Color.Cyan, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your power has grown.", color = Color.LightGray)
                    Text("New Skill: $evolvedName", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = { viewModel.clearEvolutionBanner() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)) {
                        Text("EXCELLENT", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
