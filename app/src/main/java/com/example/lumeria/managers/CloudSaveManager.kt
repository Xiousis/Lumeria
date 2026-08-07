package com.example.lumeria.managers

import android.app.Activity
import android.util.Log
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.SnapshotsClient
import com.google.android.gms.games.snapshot.Snapshot
import com.google.android.gms.games.snapshot.SnapshotMetadataChange
import kotlinx.coroutines.tasks.await
import java.io.IOException

class CloudSaveManager(private val activity: Activity) {

    private val TAG = "CloudSaveManager"
    
    private val snapshotsClient: SnapshotsClient by lazy {
        PlayGames.getSnapshotsClient(activity)
    }

    private val signInClient by lazy {
        PlayGames.getGamesSignInClient(activity)
    }

    suspend fun isAuthenticated(): Boolean {
        return try {
            val result = signInClient.isAuthenticated.await()
            result.isAuthenticated
        } catch (e: Exception) {
            Log.e(TAG, "Auth check failed", e)
            false
        }
    }

    /**
     * Saves the provided JSON string to the cloud for a specific slot.
     */
    suspend fun saveToCloud(slot: Int, data: String, description: String): Boolean {
        if (!isAuthenticated()) return false
        val snapshotName = "Lumeria_Slot_$slot"

        return try {
            val result = snapshotsClient.open(snapshotName, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED).await()
            
            if (result.isConflict) {
                // For simplicity, we assume the policy handles it, 
                // but we could do manual resolution here if needed.
                Log.w(TAG, "Conflict detected for slot $slot, using most recent.")
            }
            
            val snapshot = result.data
            snapshot?.let {
                it.snapshotContents.writeBytes(data.toByteArray())
                val metadataChange = SnapshotMetadataChange.Builder()
                    .setDescription(description)
                    .build()
                
                snapshotsClient.commitAndClose(it, metadataChange).await()
                Log.d(TAG, "Cloud Save successful for slot $slot")
                true
            } ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Cloud Save failed for slot $slot", e)
            false
        }
    }

    /**
     * Loads the JSON string from the cloud for a specific slot.
     */
    suspend fun loadFromCloud(slot: Int): String? {
        if (!isAuthenticated()) return null
        val snapshotName = "Lumeria_Slot_$slot"

        return try {
            val result = snapshotsClient.open(snapshotName, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED).await()
            val snapshot = result.data
            
            snapshot?.let {
                val bytes = it.snapshotContents.readFully()
                snapshotsClient.commitAndClose(it, SnapshotMetadataChange.EMPTY_CHANGE).await()
                String(bytes)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cloud Load failed for slot $slot", e)
            null
        }
    }
}
