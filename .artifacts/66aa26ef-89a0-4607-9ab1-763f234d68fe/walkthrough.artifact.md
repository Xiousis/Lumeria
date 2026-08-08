# God Tier Unique Battle Skills Walkthrough

I have added three unique battle skills that are exclusively available to players with God Tier traits. I also added a third God Tier trait to complete the set.

## New God Tier Trait
- **Will of the Ancients**: Added as the third God Tier trait.
    - **Stats**: +150 to all stats, +150 Max HP/Mana.
    - **Special Effects**: +50 HP/Mana Regeneration per turn and **Absolute Resistance** (immune to stun and defense debuffs).

## Unique Battle Skills
These skills only appear in the skill selection screen if the player has the required trait.

| Skill | Required Trait | Effect |
| :--- | :--- | :--- |
| **Ichor Surge** | Blood of the Gods | Massive damage (15x multiplier) and recovers 50% of damage dealt as HP. |
| **Starlight Judgment** | Eyes of the Creator | Strikes 5 times, ignoring all enemy defense. |
| **Ancient Dominion** | Will of the Ancients | Deals colossal damage (25x multiplier) with a high chance to stun the enemy. |

## Technical Implementation Details
### 1. Skill Access Logic
Updated `SkillScreen.kt` to dynamically filter the learned skills list based on the player's unlocked traits:
```kotlin
val unlockedSkills = SkillDatabase.skills.filter { skill ->
    skill.name in playerData.unlockedSkills ||
    (skill.name == "Ichor Surge" && playerData.unlockedTraits.contains("Blood of the Gods")) ||
    (skill.name == "Starlight Judgment" && playerData.unlockedTraits.contains("Eyes of the Creator")) ||
    (skill.name == "Ancient Dominion" && playerData.unlockedTraits.contains("Will of the Ancients"))
}
```

### 2. Battle Mechanics
- **BattleLogic.kt**:
    - Added `MultiHitIgnore` logic to handle multi-hits that bypass armor.
    - Added `HealAttack` logic to provide 50% lifesteal.
    - Updated `Starlight Judgment` to trigger exactly 5 hits.
- **BattleEngine.kt**:
    - Added regeneration logic for **Will of the Ancients** (+50 HP/Mana).
    - Implemented **Absolute Resistance** in `applyEnemyAttack` to block incoming stun and defense-down effects.

## Files Modified
- [TraitDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/database/TraitDatabase.kt)
- [SkillDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/database/SkillDatabase.kt)
- [SkillScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/screens/SkillScreen.kt)
- [BattleLogic.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/battle/BattleLogic.kt)
- [BattleEngine.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/battle/BattleEngine.kt)
