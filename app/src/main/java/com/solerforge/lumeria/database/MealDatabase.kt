package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.BuffType
import com.solerforge.lumeria.models.Meal
import com.solerforge.lumeria.models.PlayerBuff

object MealDatabase {
    val meals = listOf(
        Meal(
            "Hard-Boiled Slime Egg",
            "A cheap, rubbery snack. +5% XP for 3 battles.",
            800,
            PlayerBuff(BuffType.Experience, 1.05, 3, "Slime Egg Energy", isFood = true)
        ),
        Meal(
            "Forest Berry Tart",
            "Sweet and energizing. +10% Mana Regen for 5 battles.",
            2500,
            PlayerBuff(BuffType.ManaRegen, 1.10, 5, "Berry Focus", isFood = true)
        ),
        Meal(
            "Grilled Boar Ribs",
            "Hearty and filling. +8% Damage for 5 battles.",
            5000,
            PlayerBuff(BuffType.Damage, 1.08, 5, "Boar's Might", isFood = true)
        ),
        Meal(
            "Mountain Herb Stew",
            "A bitter but bracing broth. +10% Defense for 5 battles.",
            4500,
            PlayerBuff(BuffType.Defense, 1.10, 5, "Herbal Fortitude", isFood = true)
        ),
        Meal(
            "Dragonscale Omelette",
            "A luxury meal fit for a hero. +15% XP for 8 battles.",
            25000,
            PlayerBuff(BuffType.Experience, 1.15, 8, "Dragon's Ambition", isFood = true)
        ),
        Meal(
            "Celestial Ambrosia",
            "Tastes like pure starlight. +12% Gold for 5 battles.",
            150000,
            PlayerBuff(BuffType.Gold, 1.12, 5, "Divine Favor", isFood = true)
        )
    )

    fun getMeal(name: String): Meal? {
        return meals.find { it.name == name }
    }
}
