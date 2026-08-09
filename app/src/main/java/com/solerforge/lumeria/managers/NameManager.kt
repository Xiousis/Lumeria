package com.solerforge.lumeria.managers

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object NameManager {
    private val NAMES_COLLECTION = "names"

    suspend fun isNameAvailable(name: String): Boolean {
        if (name.isBlank() || name.length < 3) return false
        // Bypass for testing if needed, but for now strict
        if (name.lowercase().trim() == "xious") return false
        
        return try {
            val db = FirebaseFirestore.getInstance()
            val doc = db.collection(NAMES_COLLECTION).document(name.lowercase().trim()).get().await()
            !doc.exists()
        } catch (e: Exception) {
            // HIGH: If Firestore fails (offline/timeout), we shouldn't falsely say available
            // but for user testing simplicity we'll allow it if the error is "Permission Denied"
            // actually let's return false to be safe on the server side.
            false 
        }
    }

    suspend fun claimName(name: String, deviceId: String): Boolean {
        val nameId = name.lowercase().trim()
        return try {
            val db = FirebaseFirestore.getInstance()
            db.runTransaction { transaction ->
                val docRef = db.collection(NAMES_COLLECTION).document(nameId)
                val snapshot = transaction.get(docRef)
                
                if (snapshot.exists()) {
                    val owner = snapshot.getString("owner")
                    if (owner != deviceId) {
                        throw Exception("Name already taken")
                    }
                }
                
                transaction.set(docRef, mapOf(
                    "owner" to deviceId,
                    "playerName" to name,
                    "timestamp" to System.currentTimeMillis()
                ))
            }.await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
