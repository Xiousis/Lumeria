package com.solerforge.lumeria.viewmodels

import androidx.lifecycle.ViewModel
import com.solerforge.lumeria.data.PlayerData

class EconomyViewModel : ViewModel() {

    fun deposit(amount: Long, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        if (amount <= 0L) return
        if (playerData.gold >= amount) {
            val updated = playerData.copy(
                gold = playerData.gold - amount,
                bankGold = playerData.bankGold + amount
            )
            onUpdatePlayer(updated)
        }
    }

    fun withdraw(amount: Long, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        if (amount <= 0L) return
        if (playerData.bankGold >= amount) {
            val updated = playerData.copy(
                gold = playerData.gold + amount,
                bankGold = playerData.bankGold - amount
            )
            onUpdatePlayer(updated)
        }
    }
}
