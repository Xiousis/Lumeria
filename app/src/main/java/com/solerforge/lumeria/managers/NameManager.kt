package com.solerforge.lumeria.managers

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object NameManager {
    private val db = FirebaseFirestore.getInstance()
    private val NAMES_COLLECTION = "names"

    suspend fun isNameAvailable(name: String): Boolean {
        if (name.isBlank() || name.length < 3) return false
        return try {
            val doc = db.collection(NAMES_COLLECTION).document(name.lowercase().trim()).get().await()
            !doc.exists()
        } catch (e: Exception) {
            // If check fails (offline?), we might want to allow it or fail safe.
            // For true uniqueness, we should require online.
            true 
        }
    }

    suspend fun claimName(name: String, deviceId: String): Boolean {
        val nameId = name.lowercase().trim()
        return try {
            db.runTransaction { transaction ->
                val docRef = db.collection(NAMES_COLLECTION).document(nameId)
                val snapshot = transaction.get(docRef)
                
                if (snapshot.exists()) {
                    val owner = snapshot.getString("owner")
                    if (owner != deviceId) {
                        throw Exception("Name already taken")
                    }
                    // If same device, we just re-confirm it
                }
                
                transaction.set(docRef, mapOf(
                    "owner" to deviceId,
                    "playerName" to name, // Keep original casing
                    "timestamp" to System.currentTimeMillis()
                ))
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
