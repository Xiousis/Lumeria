package com.solerforge.lumeria.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import android.util.Base64
import com.solerforge.lumeria.managers.CloudSaveManager
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
        fun backupKey(slot: Int) = stringPreferencesKey("player_data_backup_slot_$slot")
        val LAST_USED_SLOT = intPreferencesKey("last_used_slot")
        val SETTINGS = stringPreferencesKey("game_settings")
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val CURRENT_SAVE_VERSION = 5

    // This is a secret key used for signing save files. 
    // In production, this is injected from local.properties via BuildConfig.
    private val SIGN_KEY = com.solerforge.lumeria.BuildConfig.SAVE_SIGN_KEY.toByteArray()

    private fun migrateIfNeeded(data: PlayerData): PlayerData {
        var currentData = data
        
        // Migration loop
        while (currentData.saveVersion < CURRENT_SAVE_VERSION) {
            currentData = when (currentData.saveVersion) {
                1 -> migrateV1ToV2(currentData)
                2 -> migrateV2ToV3(currentData)
                3 -> currentData.copy(saveVersion = 4)
                4 -> currentData.copy(saveVersion = 5)
                else -> throw IllegalStateException("Unknown save version: ${currentData.saveVersion}")
            }
        }
        
        return currentData.checkFeatureUnlocks().recalculateVitals()
    }

    private fun migrateV1ToV2(data: PlayerData): PlayerData {
        return data.copy(
            saveVersion = 2,
            playerName = "Xious",
            gender = "Male",
            playerClass = "Warrior",
            isReborn = false,
            legacyHeroStats = null
        )
    }

    private fun migrateV2ToV3(data: PlayerData): PlayerData {
        // Example: Add a new field or adjust existing ones
        return data.copy(saveVersion = 3)
    }

    private fun generateSignature(data: String): String {
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(SIGN_KEY, "HmacSHA256")
        hmacSha256.init(secretKey)
        val hash = hmacSha256.doFinal(data.toByteArray())
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    data class SaveResult(
        val data: PlayerData,
        val status: SaveStatus
    )

    fun getPlayerDataFlow(slot: Int): Flow<SaveResult> {
        return context.dataStore.data.map { preferences ->
            val jsonString = preferences[Keys.slotKey(slot)]
            if (jsonString != null) {
                try {
                    val decoded = json.decodeFromString<PlayerData>(jsonString)
                    SaveResult(migrateIfNeeded(decoded), SaveStatus.Loaded)
                } catch (e: Exception) {
                    // Try to restore from backup if primary is corrupt
                    val backupJson = preferences[Keys.backupKey(slot)]
                    if (backupJson != null) {
                        try {
                            val decodedBackup = json.decodeFromString<PlayerData>(backupJson)
                            SaveResult(migrateIfNeeded(decodedBackup), SaveStatus.RecoveredFromBackup)
                        } catch (backupEx: Exception) {
                            SaveResult(PlayerData(), SaveStatus.Corrupt)
                        }
                    } else {
                        SaveResult(PlayerData(), SaveStatus.Corrupt)
                    }
                }
            } else {
                SaveResult(PlayerData(), SaveStatus.Empty)
            }
        }
    }

    suspend fun updatePlayer(slot: Int, transform: (PlayerData) -> PlayerData): PlayerData {
        var finalData: PlayerData? = null
        context.dataStore.edit { preferences ->
            val jsonString = preferences[Keys.slotKey(slot)]
            var validJsonForBackup: String? = null
            
            val currentData = if (jsonString != null) {
                try {
                    val decoded = json.decodeFromString<PlayerData>(jsonString)
                    validJsonForBackup = jsonString
                    migrateIfNeeded(decoded)
                } catch (e: Exception) {
                    val backupJson = preferences[Keys.backupKey(slot)]
                    if (backupJson != null) {
                        try {
                            val decodedBackup = json.decodeFromString<PlayerData>(backupJson)
                            migrateIfNeeded(decodedBackup)
                        } catch (backupEx: Exception) {
                            throw IllegalStateException("Save data for slot $slot is critically corrupted.")
                        }
                    } else {
                        throw IllegalStateException("Save data for slot $slot is corrupted and no backup exists.")
                    }
                }
            } else {
                PlayerData() // New slot
            }

            val updatedData = transform(currentData).copy(
                saveVersion = CURRENT_SAVE_VERSION,
                lastSavedAt = System.currentTimeMillis()
            )
            val updatedJson = json.encodeToString(updatedData)

            if (validJsonForBackup != null) {
                preferences[Keys.backupKey(slot)] = validJsonForBackup
            }

            preferences[Keys.slotKey(slot)] = updatedJson
            preferences[Keys.LAST_USED_SLOT] = slot
            
            finalData = updatedData
        }
        
        val result = finalData ?: throw IllegalStateException("Update failed to produce final data")

        // Trigger Cloud Save if enabled
        val settings = settingsFlow.first()
        if (settings.cloudSaveEnabled) {
            cloudSaveManager?.saveToCloud(
                slot = slot,
                data = json.encodeToString(result),
                description = "Lv. ${result.level} - ${result.currentLocation}"
            )
        }

        return result
    }

    suspend fun savePlayerData(playerData: PlayerData, slot: Int): PlayerData {
        return updatePlayer(slot) { playerData }
    }

    suspend fun syncFromCloud(slot: Int): Boolean {
        val cloudDataJson = cloudSaveManager?.loadFromCloud(slot) ?: return false
        return try {
            val cloudDecoded = json.decodeFromString<PlayerData>(cloudDataJson)
            val cloudMigrated = migrateIfNeeded(cloudDecoded)
            
            // Check local data
            val localPreferences = context.dataStore.data.first()
            val localJsonString = localPreferences[Keys.slotKey(slot)]
            
            val localData = localJsonString?.let {
                runCatching {
                    json.decodeFromString<PlayerData>(it)
                }.getOrNull()?.let { migrateIfNeeded(it) }
            }
            
            if (localData != null && localData.lastSavedAt > cloudMigrated.lastSavedAt) {
                return false // Local is newer
            }

            // Save to local
            context.dataStore.edit { preferences ->
                // Backup current local before overwriting with cloud, only if local was valid
                if (localData != null && localJsonString != null) {
                    preferences[Keys.backupKey(slot)] = localJsonString
                }
                preferences[Keys.slotKey(slot)] = json.encodeToString(cloudMigrated)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun clearPlayerData(slot: Int) {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.slotKey(slot))
            preferences.remove(Keys.backupKey(slot))
        }
        // Also clear cloud if enabled
        val settings = settingsFlow.first()
        if (settings.cloudSaveEnabled) {
            cloudSaveManager?.deleteFromCloud(slot)
        }
    }

    suspend fun restoreBackup(slot: Int): Boolean {
        return try {
            var restored = false
            context.dataStore.edit { preferences ->
                val backupJson = preferences[Keys.backupKey(slot)]
                if (backupJson != null) {
                    // Validate backup before restoring
                    val decoded = json.decodeFromString<PlayerData>(backupJson)
                    if (decoded.playerName.isNotEmpty()) {
                        preferences[Keys.slotKey(slot)] = backupJson
                        restored = true
                    }
                }
            }
            restored
        } catch (e: Exception) {
            false
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
