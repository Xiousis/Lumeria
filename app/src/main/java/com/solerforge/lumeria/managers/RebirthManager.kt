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
        val targetBosses = if (playerData.isReborn) BossDatabase.bosses else BossDatabase.bosses.filter { !it.isLegacy }
        val worldBossesCount = targetBosses.size
        val defeatedWorldBosses = playerData.defeatedBosses.intersect(targetBosses.map { it.name }.toSet()).size
        
        val arenaOpponentsCount = ArenaEnemyDatabase.opponents.size
        val defeatedArenaOpponents = playerData.completedArenaOpponents.size

        val mainStoryArcs = StoryDatabase.arcs.filter { it.id in 1..20 }
        val storyArcsCount = mainStoryArcs.size
        val completedStoryArcs = playerData.completedStoryArcs.count { id -> mainStoryArcs.any { it.id == id } }

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
