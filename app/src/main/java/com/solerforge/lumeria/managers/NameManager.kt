package com.solerforge.lumeria.managers

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import android.util.Log

object NameManager {
    private val NAMES_COLLECTION = "names"

    suspend fun isNameAvailable(name: String, deviceId: String? = null): Boolean {
        if (name.isBlank() || name.length !in 3..12) return false
        // Bypass for testing if needed, but for now strict
        if (name.lowercase().trim() == "xious") return false
        
        return try {
            val db = FirebaseFirestore.getInstance()
            // Add a timeout to prevent being stuck on "Checking..." if Firestore is offline/unconfigured
            val doc = withTimeoutOrNull(3000) {
                db.collection(NAMES_COLLECTION).document(name.lowercase().trim()).get().await()
            }
            
            if (doc == null) {
                Log.w("NameManager", "Name check timed out for '$name'.")
                return false
            }
            
            if (!doc.exists()) return true
            
            // If name exists, check if it's owned by the current device
            val owner = doc.getString("owner")
            owner == deviceId
        } catch (e: Exception) {
            Log.e("NameManager", "Error checking name availability: ${e.message}")
            false
        }
    }

    suspend fun claimName(name: String, deviceId: String): Boolean {
        if (name.length !in 3..12) return false
        val nameId = name.lowercase().trim()
        return try {
            val db = FirebaseFirestore.getInstance()
            val result = withTimeoutOrNull(5000) {
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
            }
            
            if (result == null) {
                Log.w("NameManager", "Claim name timed out.")
                false
            } else {
                result
            }
        } catch (e: Exception) {
            if (e.message == "Name already taken") {
                false
            } else {
                Log.e("NameManager", "Error claiming name: ${e.message}.")
                false
            }
        }
    }
}
