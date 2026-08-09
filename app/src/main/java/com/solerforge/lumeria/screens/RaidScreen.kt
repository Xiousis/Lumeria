package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.RpgButton

@Composable
fun RaidScreen(
    onReturn: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BATTLE RAIDS",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Red,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Assemble your allies. Conquer the impossible.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "COMING SOON",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(64.dp))

            RpgButton(
                text = "RETURN",
                onClick = onReturn,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}
