package com.example.lumeria.models

data class Skill(
    val name: String,
    val description: String,
    val manaCost: Int,
    val cooldown: Int,
    val unlockLevel: Int,
    val skillType: String, // "Attack", "Support", "Buff"
    val multiplier: Double = 1.0,
    val effectType: String = "Normal", // "Stun", "Bleed", "Buff", "IgnoreArmor", "MultiHit", "Heal", "Parry"
    val elementType: ElementType = ElementType.Physical,
    val statusEffect: StatusEffect = StatusEffect.None,
    val evolvesTo: String? = null
)
