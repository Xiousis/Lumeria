package com.solerforge.lumeria.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.R

@Composable
fun FantasyBorder(
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    useBackground: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .then(if (useBackground) Modifier.background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(8.dp)) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // 1. The Frame Artwork
        Image(
            painter = painterResource(id = R.drawable.item_border),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds
        )

        // 2. Rarity Glow (Optional)
        if (borderColor != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(
                        width = borderWidth,
                        color = borderColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }

        // 3. Content - Using large horizontal padding to fit within the decorative asset's inner area
        Box(modifier = Modifier.padding(horizontal = 48.dp, vertical = 20.dp)) {
            content()
        }
    }
}

@Composable
fun PortraitFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 1. The Portrait
        Box(
            modifier = Modifier
                .fillMaxSize(0.85f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            content()
        }

        // 2. The Frame Overlay (No background, just the edges)
        Image(
            painter = painterResource(id = R.drawable.item_border),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}
