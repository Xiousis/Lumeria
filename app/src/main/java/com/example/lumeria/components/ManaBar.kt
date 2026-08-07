package com.example.lumeria.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ProgressBar
import com.example.lumeria.R

@Composable
fun ManaBar(current: Int, max: Int, modifier: Modifier = Modifier, isHero: Boolean = false) {
    if (isHero) {
        HeroManaBar(current, max, modifier)
    } else {
        StandardManaBar(current, max, modifier)
    }
}

@Composable
fun StandardManaBar(current: Int, max: Int, modifier: Modifier = Modifier) {
    val fraction = if (max > 0) current.toFloat() / max else 0f
    val safeFraction = fraction.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = safeFraction,
        animationSpec = tween(600),
        label = "StandardManaBarWidth"
    )

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(16.dp),
        factory = { context ->
            ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                progressDrawable = context.getDrawable(R.drawable.standard_mana_progress)
                this.max = 1000
                setPadding(0, 0, 0, 0)
                minimumHeight = 0
            }
        },
        update = { progressBar ->
            progressBar.progress = (animatedProgress * 1000).toInt()
        }
    )
}

@Composable
fun HeroManaBar(
    current: Int, 
    max: Int, 
    modifier: Modifier = Modifier,
    barHeight: Dp = 54.dp
) {
    val fraction = if (max > 0) current.toFloat() / max else 0f
    val safeFraction = fraction.coerceIn(0f, 1f)
    
    val animatedProgress by animateFloatAsState(
        targetValue = safeFraction,
        animationSpec = tween(600),
        label = "HeroManaBarWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight),
        contentAlignment = Alignment.CenterStart
    ) {
        // ProgressBar now contains both the fill and the art frame
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                    progressDrawable = context.getDrawable(R.drawable.hero_mana_progress)
                    this.max = 1000
                    setPadding(0, 0, 0, 0)
                    minimumHeight = 0
                }
            },
            update = { progressBar ->
                progressBar.progress = (animatedProgress * 1000).toInt()
            }
        )
    }
}
