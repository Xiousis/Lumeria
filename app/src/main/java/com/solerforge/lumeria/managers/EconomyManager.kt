package com.solerforge.lumeria.managers

import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.models.Screen

class EconomyManager(
    private val onUpdatePlayer: (PlayerData) -> Unit,
    private val onNavigate: (Screen) -> Unit,
    private val onReplace: (Screen) -> Unit
) {
    fun enterShop(player: PlayerData) {
        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(player.playerRace)
        val isMonster = player.isReborn && race.isMonster
        
        if (isMonster) {
            if (!player.visitedBilly) onNavigate(Screen.BlackMarketIntro)
            else onNavigate(Screen.BlackMarket)
        } else {
            if (!player.visitedBilly) onNavigate(Screen.BillyIntro)
            else onNavigate(Screen.Shop)
        }
    }

    fun completeBillyIntro(player: PlayerData) {
        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(player.playerRace)
        val isMonster = player.isReborn && race.isMonster
        
        onUpdatePlayer(player.copy(visitedBilly = true))
        if (isMonster) onReplace(Screen.BlackMarket)
        else onReplace(Screen.Shop)
    }

    fun enterInn(player: PlayerData) {
        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(player.playerRace)
        val isMonster = player.isReborn && race.isMonster

        if (isMonster) {
            if (!player.visitedInn) onNavigate(Screen.LairIntro)
            else onNavigate(Screen.TheLair)
        } else {
            if (!player.visitedInn) onNavigate(Screen.InnIntro)
            else onNavigate(Screen.Inn)
        }
    }

    fun completeInnIntro(player: PlayerData) {
        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(player.playerRace)
        val isMonster = player.isReborn && race.isMonster

        onUpdatePlayer(player.copy(visitedInn = true))
        if (isMonster) onReplace(Screen.TheLair)
        else onReplace(Screen.Inn)
    }

    fun enterForge(player: PlayerData) {
        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(player.playerRace)
        val isMonster = player.isReborn && race.isMonster

        if (isMonster) {
            if (!player.visitedForge) onNavigate(Screen.ArmoryIntro)
            else onNavigate(Screen.TheArmory)
        } else {
            if (!player.visitedForge) onNavigate(Screen.ForgeIntro)
            else onNavigate(Screen.Blacksmith)
        }
    }

    fun completeForgeIntro(player: PlayerData) {
        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(player.playerRace)
        val isMonster = player.isReborn && race.isMonster

        onUpdatePlayer(player.copy(visitedForge = true))
        if (isMonster) onReplace(Screen.TheArmory)
        else onReplace(Screen.Blacksmith)
    }
}
