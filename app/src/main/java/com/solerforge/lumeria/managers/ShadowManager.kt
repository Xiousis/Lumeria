package com.solerforge.lumeria.managers

import android.util.Log
import com.solerforge.lumeria.data.PlayerData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object ShadowManager {

    private val db = FirebaseFirestore.getInstance()
    private val SHADOWS_COLLECTION = "player_shadows"

    suspend fun uploadShadow(playerData: PlayerData, deviceId: String) {
        if (!playerData.isReborn) return

        val shadowData = mapOf(
            "name" to playerData.playerName,
            "level" to playerData.level,
            "class" to playerData.playerClass,
            "gender" to playerData.gender,
            "hp" to playerData.maxHp,
            "mana" to playerData.maxMana,
            "strength" to playerData.strength,
            "defense" to playerData.defense,
            "agility" to playerData.agility,
            "intelligence" to playerData.intelligence,
            "pvpWins" to playerData.pvpWins,
            "pvpLosses" to playerData.pvpLosses,
            "randomSort" to Math.random(),
            "lastUpdated" to System.currentTimeMillis()
        )

        try {
            db.collection(SHADOWS_COLLECTION).document(deviceId).set(shadowData).await()
            Log.d("ShadowManager", "Shadow uploaded successfully")
        } catch (e: Exception) {
            Log.e("ShadowManager", "Shadow upload failed", e)
        }
    }

    suspend fun fetchRandomShadows(count: Int = 3): List<PlayerData> {
        return try {
            val randomVal = Math.random()
            val result = db.collection(SHADOWS_COLLECTION)
                .whereGreaterThanOrEqualTo("randomSort", randomVal)
                .limit(count.toLong())
                .get()
                .await()
            
            // If we don't have enough documents "above" the random value, fetch from the start
            val docs = if (result.size() < count) {
                db.collection(SHADOWS_COLLECTION)
                    .limit(count.toLong())
                    .get()
                    .await()
                    .documents
            } else {
                result.documents
            }

            docs.map { doc ->
                PlayerData(
                    playerName = doc.getString("name") ?: "Mysterious Wanderer",
                    level = doc.getLong("level")?.toInt() ?: 1,
                    playerClass = doc.getString("class") ?: "Warrior",
                    gender = doc.getString("gender") ?: "Male",
                    maxHp = doc.getLong("hp")?.toInt() ?: 30,
                    hp = doc.getLong("hp")?.toInt() ?: 30,
                    maxMana = doc.getLong("mana")?.toInt() ?: 20,
                    mana = doc.getLong("mana")?.toInt() ?: 20,
                    strength = doc.getLong("strength")?.toInt() ?: 5,
                    defense = doc.getLong("defense")?.toInt() ?: 5,
                    agility = doc.getLong("agility")?.toInt() ?: 5,
                    intelligence = doc.getLong("intelligence")?.toInt() ?: 5,
                    isReborn = true
                )
            }
        } catch (e: Exception) {
            Log.e("ShadowManager", "Shadow fetch failed", e)
            emptyList()
        }
    }
}
