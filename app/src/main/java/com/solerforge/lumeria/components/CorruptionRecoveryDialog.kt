package com.solerforge.lumeria.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CorruptionRecoveryDialog(
    onRestoreBackup: () -> Unit,
    onStartFresh: () -> Unit,
    onRestoreCloud: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "DATA CORRUPTION DETECTED", 
                fontWeight = FontWeight.Bold,
                color = Color.Red
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "We encountered an error while loading your save data. This usually happens if the app is closed during a save operation.",
                    color = Color.White
                )
                Text(
                    "Would you like to attempt a restoration from a local backup or the cloud? Alternatively, you can start a fresh game.",
                    color = Color.LightGray
                )
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RpgButton(
                    text = "RESTORE LOCAL BACKUP",
                    onClick = {
                        onRestoreBackup()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                RpgButton(
                    text = "SYNC FROM CLOUD",
                    onClick = {
                        onRestoreCloud()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color(0xFF455A64)
                )
                Button(
                    onClick = onStartFresh,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("START FRESH (DELETE SAVE)", color = Color.White)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Gray)
            }
        },
        containerColor = Color(0xFF121212),
        titleContentColor = Color.Red,
        textContentColor = Color.White
    )
}
