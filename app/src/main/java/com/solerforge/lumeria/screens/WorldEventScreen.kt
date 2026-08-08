package com.solerforge.lumeria.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.models.WorldEvent

@Composable
fun WorldEventScreen(
    event: WorldEvent,
    outcomeMessage: String?,
    onOptionSelected: (com.solerforge.lumeria.models.EventOutcome) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E293B).copy(alpha = 0.6f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "World Event",
            color = Color.Cyan,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = event.title,
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(20.dp)
        ) {
            Text(
                text = event.description,
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (outcomeMessage == null) {
            // Options
            event.options.forEach { option ->
                RpgButton(
                    text = option.text,
                    onClick = { onOptionSelected(option.outcome) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        } else {
            // Result
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = outcomeMessage,
                    color = Color.Yellow,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            RpgButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
