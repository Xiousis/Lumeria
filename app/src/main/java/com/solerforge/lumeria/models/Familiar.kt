package com.solerforge.lumeria.models

import kotlinx.serialization.Serializable

@Serializable
data class Familiar(
    val name: String,
    val description: String,
    val rarity: String,
    val actionType: FamiliarActionType,
    val basePower: Int,
    val price: Int = 0,
    val supportInterval: Int = 3, // Every X turns
    val element: ElementType = ElementType.Physical,
    val requiredClasses: List<String> = emptyList()
)

enum class FamiliarActionType {
    Damage, // Attacks the enemy
    Heal,   // Heals the player
    Mana,   // Restores mana
    Buff,   // Increases damage for next turn
    Debuff  // Decreases enemy defense
}
