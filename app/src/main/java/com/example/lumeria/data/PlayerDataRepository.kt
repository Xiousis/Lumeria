package com.example.lumeria.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import android.util.Base64
import com.example.lumeria.managers.CloudSaveManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "player_prefs")

class PlayerDataRepository(
    private val context: Context,
    var cloudSaveManager: CloudSaveManager? = null
) {

    private object Keys {
        fun slotKey(slot: Int) = stringPreferencesKey("player_data_slot_$slot")
        val LAST_USED_SLOT = intPreferencesKey("last_used_slot")
        val SETTINGS = stringPreferencesKey("game_settings")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val CURRENT_SAVE_VERSION = 1

    // This is a secret key used for signing save files. 
    // In production, this is injected from local.properties via BuildConfig.
    private val SIGN_KEY = com.example.lumeria.BuildConfig.SAVE_SIGN_KEY.toByteArray()

    private fun migrateIfNeeded(data: PlayerData): PlayerData {
        var currentData = data
        
        // Migration loop
        while (currentData.saveVersion < CURRENT_SAVE_VERSION) {
            currentData = when (currentData.saveVersion) {
                // Add future migration cases here:
                // 1 -> performMigrationToV2(currentData)
                else -> currentData.copy(saveVersion = CURRENT_SAVE_VERSION)
            }
        }
        
        return currentData
    }

    private fun generateSignature(data: String): String {
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(SIGN_KEY, "HmacSHA256")
        hmacSha256.init(secretKey)
        val hash = hmacSha256.doFinal(data.toByteArray())
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun getPlayerDataFlow(slot: Int): Flow<PlayerData?> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[Keys.slotKey(slot)]
            if (jsonString != null) {
                try {
                    val decoded = json.decodeFromString<PlayerData>(jsonString)
                    migrateIfNeeded(decoded)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    suspend fun savePlayerData(playerData: PlayerData, slot: Int) {
        val dataToSave = playerData.copy(saveVersion = CURRENT_SAVE_VERSION)
        val jsonString = json.encodeToString(dataToSave)
        context.dataStore.edit { preferences ->
            preferences[Keys.slotKey(slot)] = jsonString
            preferences[Keys.LAST_USED_SLOT] = slot
        }

        // Trigger Cloud Save if enabled
        val settings = settingsFlow.first()
        if (settings.cloudSaveEnabled) {
            cloudSaveManager?.saveToCloud(
                slot = slot,
                data = jsonString,
                description = "Lv. ${playerData.level} - ${playerData.currentLocation}"
            )
        }
    }

    suspend fun syncFromCloud(slot: Int): Boolean {
        val cloudDataJson = cloudSaveManager?.loadFromCloud(slot) ?: return false
        return try {
            val decoded = json.decodeFromString<PlayerData>(cloudDataJson)
            val migrated = migrateIfNeeded(decoded)
            
            // Save to local
            context.dataStore.edit { preferences ->
                preferences[Keys.slotKey(slot)] = json.encodeToString(migrated)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearPlayerData(slot: Int) {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.slotKey(slot))
        }
    }

    val lastUsedSlotFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[Keys.LAST_USED_SLOT] ?: 1
    }

    val settingsFlow: Flow<GameSettings> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[Keys.SETTINGS]
        if (jsonString != null) {
            try {
                json.decodeFromString<GameSettings>(jsonString)
            } catch (e: Exception) {
                GameSettings()
            }
        } else {
            GameSettings()
        }
    }

    suspend fun saveSettings(settings: GameSettings) {
        val jsonString = json.encodeToString(settings)
        context.dataStore.edit { preferences ->
            preferences[Keys.SETTINGS] = jsonString
        }
    }

    fun exportSave(playerData: PlayerData): String {
        val jsonString = json.encodeToString(playerData)
        val signature = generateSignature(jsonString)
        val fullData = "$signature|$jsonString"
        return Base64.encodeToString(fullData.toByteArray(), Base64.NO_WRAP)
    }

    fun importSave(saveString: String): PlayerData? {
        return try {
            val decodedBytes = Base64.decode(saveString, Base64.DEFAULT)
            val fullData = String(decodedBytes)
            
            val parts = fullData.split("|", limit = 2)
            if (parts.size != 2) return null
            
            val signature = parts[0]
            val jsonString = parts[1]
            
            // Verify signature
            if (signature != generateSignature(jsonString)) {
                return null // TAMPERED!
            }
            
            val decoded = json.decodeFromString<PlayerData>(jsonString)
            migrateIfNeeded(decoded)
        } catch (e: Exception) {
            null
        }
    }
}
