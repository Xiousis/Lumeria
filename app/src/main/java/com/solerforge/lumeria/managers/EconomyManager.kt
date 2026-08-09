package com.solerforge.lumeria.managers

import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.models.Screen

class EconomyManager(
    private val onUpdatePlayer: (PlayerData) -> Unit,
    private val onNavigate: (Screen) -> Unit,
    private val onReplace: (Screen) -> Unit
) {
    fun enterShop(player: PlayerData) {
        if (!player.visitedBilly) {
            onNavigate(Screen.BillyIntro)
        } else {
            onNavigate(Screen.Shop)
        }
    }

    fun completeBillyIntro(player: PlayerData) {
        onUpdatePlayer(player.copy(visitedBilly = true))
        onReplace(Screen.Shop)
    }

    fun enterInn(player: PlayerData) {
        if (!player.visitedInn) {
            onNavigate(Screen.InnIntro)
        } else {
            onNavigate(Screen.Inn)
        }
    }

    fun completeInnIntro(player: PlayerData) {
        onUpdatePlayer(player.copy(visitedInn = true))
        onReplace(Screen.Inn)
    }

    fun enterForge(player: PlayerData) {
        if (!player.visitedForge) {
            onNavigate(Screen.ForgeIntro)
        } else {
            onNavigate(Screen.Blacksmith)
        }
    }

    fun completeForgeIntro(player: PlayerData) {
        onUpdatePlayer(player.copy(visitedForge = true))
        onReplace(Screen.Blacksmith)
    }
}
