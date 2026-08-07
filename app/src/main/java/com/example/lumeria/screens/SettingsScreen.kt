package com.example.lumeria.screens

import com.example.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.provider.Settings
import com.example.lumeria.components.RpgButton
import com.example.lumeria.data.GameSettings

@Composable
fun SettingsScreen(
    settings: GameSettings,
    onSettingsChanged: (GameSettings) -> Unit,
    onEnterCode: (String, String) -> String,
    onExportSave: () -> String,
    onImportSave: (String) -> String,
    onSyncCloudSave: ((String) -> Unit) -> Unit,
    onShowDonationDialog: () -> Unit,
    onReturn: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val deviceId = remember { Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown" }
    
    var codeText by remember { mutableStateOf("") }
    var importText by remember { mutableStateOf("") }
    var codeFeedback by remember { mutableStateOf<String?>(null) }
    var showCredits by remember { mutableStateOf(false) }
    var showLicenses by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.settings_screen_bg),
            contentDescription = "Settings Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            SettingsToggle(
                label = stringResource(R.string.settings_haptic_feedback),
                checked = settings.hapticsEnabled,
                onCheckedChange = { onSettingsChanged(settings.copy(hapticsEnabled = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsToggle(
                label = stringResource(R.string.settings_music),
                checked = settings.musicEnabled,
                onCheckedChange = { onSettingsChanged(settings.copy(musicEnabled = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsToggle(
                label = stringResource(R.string.settings_sfx),
                checked = settings.sfxEnabled,
                onCheckedChange = { onSettingsChanged(settings.copy(sfxEnabled = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsToggle(
                label = "Google Play Cloud Save",
                checked = settings.cloudSaveEnabled,
                onCheckedChange = { onSettingsChanged(settings.copy(cloudSaveEnabled = it)) }
            )

            if (settings.cloudSaveEnabled) {
                RpgButton(
                    text = "☁️ Force Sync from Cloud",
                    onClick = { 
                        onSyncCloudSave { result -> codeFeedback = result }
                    },
                    containerColor = Color(0xFF1976D2),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(stringResource(R.string.settings_redeem_code), color = Color.Gray, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = codeText,
                    onValueChange = { codeText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.settings_enter_code_placeholder)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.DarkGray,
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Button(
                    onClick = {
                        codeFeedback = onEnterCode(codeText, deviceId)
                        codeText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text(stringResource(R.string.settings_redeem_button))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.settings_support_header), color = Color.Gray, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

            val uriHandler = LocalUriHandler.current
            RpgButton(
                text = stringResource(R.string.settings_view_credits),
                onClick = { showCredits = true },
                containerColor = Color(0xFF607D8B),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            RpgButton(
                text = stringResource(R.string.settings_join_discord),
                onClick = { 
                    try {
                        uriHandler.openUri("https://discord.gg/TERMz29TD") 
                    } catch (e: Exception) {}
                },
                containerColor = Color(0xFF5865F2),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            RpgButton(
                text = stringResource(R.string.settings_buy_coffee),
                onClick = onShowDonationDialog,
                containerColor = Color(0xFF8D6E63),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.settings_legal_header), color = Color.Gray, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

            RpgButton(
                text = stringResource(R.string.settings_privacy_policy),
                onClick = { 
                    try {
                        uriHandler.openUri("https://github.com/Xiousis/Lumeria/blob/main/PRIVACY.md") 
                    } catch (e: Exception) {}
                },
                containerColor = Color(0xFF455A64),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            RpgButton(
                text = stringResource(R.string.settings_terms_of_service),
                onClick = { 
                    try {
                        uriHandler.openUri("https://github.com/Xiousis/Lumeria/blob/main/TERMS.md") 
                    } catch (e: Exception) {}
                },
                containerColor = Color(0xFF455A64),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            RpgButton(
                text = "📄 Open Source Licenses",
                onClick = { showLicenses = true },
                containerColor = Color(0xFF455A64),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Support & Data", color = Color.Gray, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "To request data deletion or support, please provide your Device ID below to the developer.",
                color = Color.LightGray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            RpgButton(
                text = "🆔 ID: $deviceId (Copy)",
                onClick = {
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(deviceId))
                    codeFeedback = "Device ID copied to clipboard."
                },
                containerColor = Color(0xFF37474F),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(stringResource(R.string.settings_save_management), color = Color.Gray, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

            val saveCopiedFeedback = stringResource(R.string.settings_save_copied_feedback)
            RpgButton(
                text = stringResource(R.string.settings_export_save),
                onClick = { 
                    val saveString = onExportSave()
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(saveString))
                    codeFeedback = saveCopiedFeedback
                },
                containerColor = Color(0xFF424242),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Text(stringResource(R.string.settings_import_save_header), color = Color.Gray, style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.Start))
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = importText,
                    onValueChange = { importText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.settings_paste_save_placeholder)) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.DarkGray,
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Button(
                    onClick = {
                        codeFeedback = onImportSave(importText)
                        importText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text(stringResource(R.string.settings_import_button))
                }
            }


            if (codeFeedback != null) {
                Text(
                    text = codeFeedback!!,
                    color = if (codeFeedback!!.contains("success", true) || codeFeedback!!.contains("copied", true)) Color.Green else Color.Yellow,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .padding(8.dp)
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )

            Text(
                text = stringResource(R.string.settings_version_format, "1.0.0"),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (showCredits) {
            AlertDialog(
                onDismissRequest = { showCredits = false },
                title = { Text("Lumeria Credits", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Lead Developer:", fontWeight = FontWeight.Bold, color = Color.Cyan)
                        Text("Jose Soler", color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Music & Sound:", fontWeight = FontWeight.Bold, color = Color.Cyan)
                        Text("Lumeria OST", color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Special Thanks:", fontWeight = FontWeight.Bold, color = Color.Cyan)
                        Text("The RPG Community & Billy the Merchant", color = Color.White)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCredits = false }) {
                        Text("Close", color = Color.Cyan)
                    }
                },
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }

        if (showLicenses) {
            AlertDialog(
                onDismissRequest = { showLicenses = false },
                title = { Text("Open Source Licenses", fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        LicenseItem("Kotlin", "Apache 2.0")
                        LicenseItem("Jetpack Compose", "Apache 2.0")
                        LicenseItem("Firebase (Analytics, Crashlytics)", "Apache 2.0")
                        LicenseItem("Google Play Billing", "Google Play Terms")
                        LicenseItem("Kotlinx Serialization", "Apache 2.0")
                        LicenseItem("AndroidX DataStore", "Apache 2.0")
                        LicenseItem("Material Design Icons", "Apache 2.0")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Full license texts available at: https://www.apache.org/licenses/LICENSE-2.0", fontSize = 10.sp, color = Color.Gray)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLicenses = false }) {
                        Text("Close", color = Color.Cyan)
                    }
                },
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}

@Composable
fun LicenseItem(name: String, license: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        Text(license, color = Color.Cyan, fontSize = 12.sp)
    }
}

@Composable
fun SettingsToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.LightGray, style = MaterialTheme.typography.titleMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Cyan,
                checkedTrackColor = Color.Cyan.copy(alpha = 0.5f)
            )
        )
    }
}
