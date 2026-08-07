package com.example.lumeria.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.lumeria.R

@Composable
fun HpBar(current: Int, max: Int, modifier: Modifier = Modifier, isHero: Boolean = false) {
    if (isHero) {
        HeroHpBar(current, max, modifier)
    } else {
        EnemyHpBar(current, max, modifier)
    }
}

@Composable
fun HeroHpBar(
    current: Int, 
    max: Int, 
    modifier: Modifier = Modifier,
    barHeight: Dp = 54.dp
) {
    val fraction = if (max > 0) current.toFloat() / max else 0f
    val safeFraction = fraction.coerceIn(0f, 1f)
    
    val animatedProgress by animateFloatAsState(
        targetValue = safeFraction,
        animationSpec = tween(500),
        label = "HeroHpBarWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight),
        contentAlignment = Alignment.CenterStart
    ) {
        // ProgressBar now contains both the fill and the art frame in its progressDrawable
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                    progressDrawable = ContextCompat.getDrawable(context, R.drawable.hero_hp_progress)
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

@Composable
fun EnemyHpBar(
    current: Int, 
    max: Int, 
    modifier: Modifier = Modifier,
    barHeight: Dp = 54.dp
) {
    val fraction = if (max > 0) current.toFloat() / max else 0f
    val safeFraction = fraction.coerceIn(0f, 1f)
    
    val animatedProgress by animateFloatAsState(
        targetValue = safeFraction,
        animationSpec = tween(500),
        label = "EnemyHpBarWidth"
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
                    progressDrawable = ContextCompat.getDrawable(context, R.drawable.enemy_hp_progress)
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
