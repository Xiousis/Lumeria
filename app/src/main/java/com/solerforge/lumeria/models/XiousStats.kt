package com.solerforge.lumeria.models

data class XiousStats(
    val level: Int = 1,
    val xp: Int = 0,
    val maxLevel: Int = 200,
) {
    companion object {
        fun getRequiredXp(level: Int): Int {
            val base = (level * level * 50) + (level * 100)
            return if (level >= 100) {
                // Steeper curve after level 100: exponential growth to make cap difficult
                val multiplier = 1.1 * Math.pow(1.05, (level - 100).toDouble())
                (base * multiplier).toInt()
            } else {
                base
            }
        }
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
        
        return copy(level = currentLevel, xp = finalXp, maxLevel = maxLevel)
    }
}
