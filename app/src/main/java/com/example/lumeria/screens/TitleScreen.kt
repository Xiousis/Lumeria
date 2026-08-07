package com.example.lumeria.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lumeria.R
import com.example.lumeria.utils.MusicManager
import kotlin.random.Random

@Composable
fun TitleScreen(onStart: () -> Unit, onSettings: () -> Unit) {

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    var startAnimation by remember { mutableStateOf(value = isPreview) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(2000),
        label = "TitleAlpha"
    )

    LaunchedEffect(Unit) { 
        startAnimation = true
        MusicManager.playMusic(context, R.raw.echoes_of_lumeria_title)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onStart() }
            .graphicsLayer(alpha = alphaAnim),
    ) {
        Image(
            painter = painterResource(id = R.drawable.title_screen),
            contentDescription = "Title Art",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight,
        )

        // SETTINGS ICON
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, end = 24.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = onSettings,
                modifier = Modifier
                    .size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings_button_icon),
                    contentDescription = "Settings",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Magic Particles Effect
        MagicParticles()
    }
}

@Composable
fun MagicParticles() {
    val particles = remember { List(15) { ParticleState() } }
    
    Box(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val infiniteTransition = rememberInfiniteTransition(label = "ParticleAnim")
            
            val xOffset by infiniteTransition.animateFloat(
                initialValue = particle.startX,
                targetValue = particle.endX,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ParticleX"
            )
            
            val yOffset by infiniteTransition.animateFloat(
                initialValue = particle.startY,
                targetValue = particle.endY,
                animationSpec = infiniteRepeatable(
                    animation = tween(particle.duration, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "ParticleY"
            )

            val alpha by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = particle.duration
                        0.0f at 0
                        0.6f at particle.duration / 2
                        0.0f at particle.duration
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "ParticleAlpha"
            )

            Box(
                modifier = Modifier
                    .offset(x = xOffset.dp, y = yOffset.dp)
                    .size(particle.size.dp)
                    .graphicsLayer(alpha = alpha)
                    .background(particle.color, CircleShape)
            )
        }
    }
}

class ParticleState {
    val startX = Random.nextFloat() * 400
    val startY = Random.nextFloat() * 800
    val endX = startX + (Random.nextFloat() - 0.5f) * 100
    val endY = startY - Random.nextFloat() * 200
    val duration = Random.nextInt(4000, 8000)
    val size = Random.nextInt(2, 6)
    val color = if (Random.nextBoolean()) Color(0xFF00E5FF) else Color(0xFFFFD700)
}
