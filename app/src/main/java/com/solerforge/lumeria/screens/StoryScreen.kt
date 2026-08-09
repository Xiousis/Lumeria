package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.utils.MusicManager

enum class IntroStep {
    LazyText, LazyImage, FinalStory
}

@Composable
fun StoryScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    var step by remember { mutableStateOf(IntroStep.LazyText) }
    
    // We use a simple alpha for the very first fade in
    var startAlpha by remember { mutableStateOf(0f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = startAlpha,
        animationSpec = tween(1500),
        label = "IntroAlpha"
    )

    var hasContinued by remember { mutableStateOf(false) }

    // Global Advance Function
    val onAdvance = {
        if (!hasContinued) {
            when (step) {
                IntroStep.LazyText -> {
                    step = IntroStep.LazyImage
                }
                IntroStep.LazyImage -> {
                    step = IntroStep.FinalStory
                }
                IntroStep.FinalStory -> {
                    hasContinued = true
                    onContinue()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.echoes_of_lumeria_title)
        startAlpha = 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onAdvance() }
    ) {
        // CONTENT LAYER
        Box(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = animatedAlpha)) {
            when (step) {
                IntroStep.LazyText -> {
                    Text(
                        text = "Before he became a hero,\nXious was the kind of kid who always said...\n\n\"I'll train tomorrow.\"",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(24.dp)
                    )
                }
                IntroStep.LazyImage -> {
                    Image(
                        painter = painterResource(id = R.drawable.lazy_xious),
                        contentDescription = "Lazy Xious",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )
                }
                IntroStep.FinalStory -> {
                    FinalStoryContent(onContinue)
                }
            }
        }

        // NAVIGATION HINT
        if (step != IntroStep.FinalStory) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 64.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "Tap to continue",
                    color = Color.White.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FinalStoryContent(onContinue: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.xious_new_life),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight,
            alignment = Alignment.Center,
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "A New Beginning",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            Text(
                text = "Xious grew up in the quiet village of Lumeria, where the mountains touched the sky and warriors were legends.\n\n" +
                        "As a child, he hated training. While other students practiced their sword swings, Xious preferred napping beneath the old oak tree.\n\n" +
                        "His wooden sword often collected more dust than experience.\n\n" +
                        "The village trainer warned him time and time again:\n\n" +
                        "\"Strength is earned, not given.\"\n\n" +
                        "Xious never listened.\n\n" +
                        "But fate had other plans.\n\n" +
                        "One day, everything would change.\n\n" +
                        "Today, his journey begins.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            RpgButton(
                text = "Begin Adventure",
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
            )
        }
    }
}
