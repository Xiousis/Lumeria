package com.solerforge.lumeria.managers

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.models.Guild
import com.solerforge.lumeria.models.GuildMember
import kotlinx.coroutines.tasks.await

class GuildManager(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val guildsCollection = db.collection("guilds")

    companion object {
        const val GUILD_CREATION_COST = 1000000L
        const val XP_PER_GOLD = 1L
    }

    suspend fun createGuild(name: String, player: PlayerData, deviceId: String): Result<String> {
        val guildId = guildsCollection.document().id
        val guild = Guild(
            id = guildId,
            name = name,
            leaderId = deviceId,
            leaderName = player.playerName,
            membersCount = 1,
            createdAt = System.currentTimeMillis()
        )

        return try {
            db.runBatch { batch ->
                batch.set(guildsCollection.document(guildId), guild)
                val member = GuildMember(
                    playerId = deviceId,
                    playerName = player.playerName,
                    playerLevel = player.level,
                    rank = "Leader"
                )
                batch.set(guildsCollection.document(guildId).collection("members").document(deviceId), member)
            }.await()
            Result.success(guildId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinGuild(guildId: String, player: PlayerData, deviceId: String): Result<Unit> {
        return try {
            val guildDoc = guildsCollection.document(guildId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(guildDoc)
                val guild = snapshot.toObject(Guild::class.java) ?: throw Exception("Guild not found")
                
                if (guild.membersCount >= guild.maxMembers) throw Exception("Guild is full")
                
                val member = GuildMember(
                    playerId = deviceId,
                    playerName = player.playerName,
                    playerLevel = player.level,
                    rank = "Member"
                )
                
                transaction.set(guildDoc.collection("members").document(deviceId), member)
                transaction.update(guildDoc, "membersCount", guild.membersCount + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAllGuilds(): Result<List<Guild>> {
        return try {
            val snapshot = guildsCollection.orderBy("membersCount", Query.Direction.DESCENDING).limit(50).get().await()
            Result.success(snapshot.toObjects(Guild::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getGuild(guildId: String): Result<Guild?> {
        return try {
            val snapshot = guildsCollection.document(guildId).get().await()
            Result.success(snapshot.toObject(Guild::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGuildMembers(guildId: String): Result<List<GuildMember>> {
        return try {
            val snapshot = guildsCollection.document(guildId).collection("members").get().await()
            Result.success(snapshot.toObjects(GuildMember::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun donateToVault(guildId: String, amount: Long, deviceId: String): Result<Unit> {
        return try {
            val guildDoc = guildsCollection.document(guildId)
            val memberDoc = guildDoc.collection("members").document(deviceId)
            
            db.runTransaction { transaction ->
                val guild = transaction.get(guildDoc).toObject(Guild::class.java) ?: throw Exception("Guild not found")
                val member = transaction.get(memberDoc).toObject(GuildMember::class.java) ?: throw Exception("Member not found")
                
                var newXp = guild.xp + (amount * XP_PER_GOLD)
                var newLevel = guild.level
                
                // Simple Level Up logic
                while (newLevel < 100) {
                    val req = com.solerforge.lumeria.database.GuildDatabase.getGuildXpForNextLevel(newLevel)
                    if (newXp >= req) {
                        newXp -= req
                        newLevel++
                    } else break
                }
                
                transaction.update(guildDoc, mapOf(
                    "guildVault" to guild.guildVault + amount,
                    "xp" to newXp,
                    "level" to newLevel
                ))
                transaction.update(memberDoc, "contributionGold", member.contributionGold + amount)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
