package com.example.lumeria.models

data class Trait(
    val name: String,
    val description: String,
    val rarity: String,
    val hpBonus: Int = 0,
    val manaBonus: Int = 0,
    val strBonus: Int = 0,
    val vitBonus: Int = 0,
    val intBonus: Int = 0,
    val agiBonus: Int = 0,
    val specialEffect: String = "None" // "Regen", "GodEyes"
)
