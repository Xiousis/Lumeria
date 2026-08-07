package com.example.lumeria.viewmodels

import androidx.lifecycle.ViewModel
import com.example.lumeria.data.PlayerData

class EconomyViewModel : ViewModel() {

    fun deposit(amount: Int, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        if (playerData.gold >= amount) {
            val updated = playerData.copy(
                gold = playerData.gold - amount,
                bankGold = playerData.bankGold + amount
            )
            onUpdatePlayer(updated)
        }
    }

    fun withdraw(amount: Int, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        if (playerData.bankGold >= amount) {
            val updated = playerData.copy(
                gold = playerData.gold + amount,
                bankGold = playerData.bankGold - amount
            )
            onUpdatePlayer(updated)
        }
    }
}
