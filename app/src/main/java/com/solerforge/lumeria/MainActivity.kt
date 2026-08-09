package com.solerforge.lumeria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.solerforge.lumeria.models.Screen
import com.solerforge.lumeria.data.PlayerDataRepository
import com.solerforge.lumeria.ui.AppNavigation
import com.solerforge.lumeria.theme.TEXTBASEDRPGMAGICTheme
import com.solerforge.lumeria.utils.SoundManager
import com.solerforge.lumeria.utils.MusicManager
import com.solerforge.lumeria.utils.SecurityUtils
import com.solerforge.lumeria.viewmodels.*
import com.solerforge.lumeria.billing.BillingManager
import com.solerforge.lumeria.managers.CloudSaveManager
import com.solerforge.lumeria.managers.GuildManager
import com.google.android.gms.games.PlayGames
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Security Audit
        val isInsecure = SecurityUtils.isEnvironmentInsecure(this)
        val isSignatureValid = SecurityUtils.isAppSignatureValid(this)

        if (isInsecure || !isSignatureValid) {
            Firebase.analytics.setUserProperty("insecure_device", "true")
            Firebase.crashlytics.setCustomKey("rooted", SecurityUtils.isDeviceRooted())
            Firebase.crashlytics.setCustomKey("emulator", SecurityUtils.isRunningOnEmulator())
            Firebase.crashlytics.setCustomKey("signature_valid", isSignatureValid)
        }

        SoundManager.init(this)
        
        PlayGames.getGamesSignInClient(this).signIn()

        val cloudSaveManager = CloudSaveManager(this)
        val repository = PlayerDataRepository(this, cloudSaveManager)
        val billingManager = BillingManager(this)
        val guildManager = GuildManager(this)
        
        val mainViewModel: MainViewModel by viewModels { MainViewModelFactory(repository) }
        
        // Feature ViewModels
        val kingdomViewModel: KingdomViewModel by viewModels()
        val economyViewModel: EconomyViewModel by viewModels()
        val adventureViewModel: AdventureViewModel by viewModels()
        val billingViewModel: BillingViewModel by viewModels { BillingViewModelFactory(billingManager) }
        val guildViewModel: GuildViewModel by viewModels { GuildViewModelFactory(guildManager) }

        enableEdgeToEdge()

        setContent {
            val playerData by mainViewModel.playerData.collectAsState()
            
            TEXTBASEDRPGMAGICTheme(isReborn = playerData.isReborn) {
                val settings by mainViewModel.settings.collectAsState()

                LaunchedEffect(settings.musicEnabled) {
                    MusicManager.isEnabled = settings.musicEnabled
                }

                LaunchedEffect(settings.sfxEnabled) {
                    SoundManager.isEnabled = settings.sfxEnabled
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                ) { innerPadding ->
                    BackHandler(enabled = mainViewModel.backstack.size > 1) {
                        val current = mainViewModel.currentScreen
                        if (current != Screen.Battle && current != Screen.Defeated && current != Screen.GamblingHouse) {
                            mainViewModel.popBackstack()
                        }
                    }

                    AppNavigation(
                        mainViewModel = mainViewModel,
                        kingdomViewModel = kingdomViewModel,
                        economyViewModel = economyViewModel,
                        adventureViewModel = adventureViewModel,
                        billingViewModel = billingViewModel,
                        guildViewModel = guildViewModel,
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        MusicManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        MusicManager.resumeMusic()
    }
}
