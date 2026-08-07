package com.example.lumeria.managers

import com.example.lumeria.data.PlayerData
import com.example.lumeria.models.Screen

class EconomyManager(
    private val onUpdatePlayer: (PlayerData) -> Unit,
    private val onNavigate: (Screen) -> Unit
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
        onNavigate(Screen.Shop)
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
        onNavigate(Screen.Inn)
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
        onNavigate(Screen.Blacksmith)
    }
}
