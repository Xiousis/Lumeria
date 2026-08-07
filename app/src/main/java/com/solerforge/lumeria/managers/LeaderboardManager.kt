package com.solerforge.lumeria.managers

import android.util.Log
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.models.LeaderboardEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

object LeaderboardManager {

    private val db = FirebaseFirestore.getInstance()
    private val LEADERBOARD_COLLECTION = "leaderboard"

    suspend fun updateLeaderboard(playerData: PlayerData, deviceId: String) {
        if (!playerData.isReborn) return

        val entry = mapOf(
            "playerName" to playerData.playerName,
            "level" to playerData.level,
            "playerClass" to playerData.playerClass,
            "pvpWins" to playerData.pvpWins,
            "pvpLosses" to playerData.pvpLosses,
            "lastUpdated" to System.currentTimeMillis()
        )

        try {
            db.collection(LEADERBOARD_COLLECTION).document(deviceId).set(entry).await()
            Log.d("LeaderboardManager", "Leaderboard updated successfully")
        } catch (e: Exception) {
            Log.e("LeaderboardManager", "Leaderboard update failed", e)
        }
    }

    suspend fun fetchGlobalRankings(limit: Int = 50): List<LeaderboardEntry> {
        return try {
            val result = db.collection(LEADERBOARD_COLLECTION)
                .orderBy("level", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            result.documents.mapNotNull { doc ->
                LeaderboardEntry(
                    playerName = doc.getString("playerName") ?: "Unknown Hero",
                    level = doc.getLong("level")?.toInt() ?: 1,
                    playerClass = doc.getString("playerClass") ?: "Warrior",
                    pvpWins = doc.getLong("pvpWins")?.toInt() ?: 0,
                    pvpLosses = doc.getLong("pvpLosses")?.toInt() ?: 0,
                    lastUpdated = doc.getLong("lastUpdated") ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e("LeaderboardManager", "Global rankings fetch failed", e)
            emptyList()
        }
    }

    suspend fun fetchPvPRankings(limit: Int = 50): List<LeaderboardEntry> {
        return try {
            val result = db.collection(LEADERBOARD_COLLECTION)
                .orderBy("pvpWins", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            result.documents.mapNotNull { doc ->
                LeaderboardEntry(
                    playerName = doc.getString("playerName") ?: "Unknown Hero",
                    level = doc.getLong("level")?.toInt() ?: 1,
                    playerClass = doc.getString("playerClass") ?: "Warrior",
                    pvpWins = doc.getLong("pvpWins")?.toInt() ?: 0,
                    pvpLosses = doc.getLong("pvpLosses")?.toInt() ?: 0,
                    lastUpdated = doc.getLong("lastUpdated") ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e("LeaderboardManager", "PvP rankings fetch failed", e)
            emptyList()
        }
    }
}
