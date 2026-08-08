package com.solerforge.lumeria.managers

import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.ArenaEnemyDatabase
import com.solerforge.lumeria.database.BossDatabase
import com.solerforge.lumeria.database.StoryDatabase

object RebirthManager {

    data class RebirthRequirement(
        val label: String,
        val currentProgress: Int,
        val targetProgress: Int,
        val isMet: Boolean
    )

    fun getRequirements(playerData: PlayerData): List<RebirthRequirement> {
        val worldBossesCount = BossDatabase.bosses.size
        val defeatedWorldBosses = playerData.defeatedBosses.intersect(BossDatabase.bosses.map { it.name }.toSet()).size
        
        val arenaOpponentsCount = ArenaEnemyDatabase.opponents.size
        val defeatedArenaOpponents = playerData.completedArenaOpponents.size

        val storyArcsCount = StoryDatabase.arcs.size
        // Assuming IDs 1-20 are the main story arcs
        val completedStoryArcs = playerData.completedStoryArcs.filter { it in 1..20 }.size

        return listOf(
            RebirthRequirement(
                label = "Level 100",
                currentProgress = playerData.level,
                targetProgress = 100,
                isMet = playerData.level >= 100
            ),
            RebirthRequirement(
                label = "Slay All World Bosses",
                currentProgress = defeatedWorldBosses,
                targetProgress = worldBossesCount,
                isMet = defeatedWorldBosses >= worldBossesCount
            ),
            RebirthRequirement(
                label = "Conquer Grand Arena",
                currentProgress = defeatedArenaOpponents,
                targetProgress = arenaOpponentsCount,
                isMet = defeatedArenaOpponents >= arenaOpponentsCount
            ),
            RebirthRequirement(
                label = "Conquer Tower of Trials",
                currentProgress = playerData.towerHighestFloor,
                targetProgress = 100,
                isMet = playerData.towerHighestFloor >= 100
            ),
            RebirthRequirement(
                label = "Complete the Legend",
                currentProgress = completedStoryArcs,
                targetProgress = storyArcsCount,
                isMet = completedStoryArcs >= storyArcsCount
            )
        )
    }

    fun canRebirth(playerData: PlayerData): Boolean {
        return !playerData.isReborn && getRequirements(playerData).all { it.isMet }
    }
}
