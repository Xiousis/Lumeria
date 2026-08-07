package com.example.lumeria.models

data class XiousStats(
    val level: Int = 1,
    val xp: Int = 0,
    val maxLevel: Int = 100,
) {
    companion object {
        fun getRequiredXp(level: Int): Int = (level * level * 50) + (level * 100)
    }

    fun gainXp(amount: Int): XiousStats {
        if (level >= maxLevel) return this
        
        var currentLevel = level
        var currentXp = xp + amount
        
        // Safety check: ensure xpNeeded is always positive
        while (currentLevel < maxLevel) {
            val needed = getRequiredXp(currentLevel)
            if (currentXp < needed) break
            
            currentXp -= needed
            currentLevel++
        }
        
        // Cap XP at 0 if we reached max level
        val finalXp = if (currentLevel >= maxLevel) 0 else currentXp
        
        return copy(level = currentLevel, xp = finalXp)
    }
}
