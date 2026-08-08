# RPG Evolution Plan: Combat, Visuals, and Bestiary

This plan details a comprehensive overhaul of "TEXT BASED RPG MAGIC," focusing on gameplay depth, visual polish, and a modern technical architecture.

## User Review Required

> [!IMPORTANT]
> **Asset Integration**: I will provide the code for dynamic backgrounds, but you will need to download the images from the provided links (or use your own) and add them to your `res/drawable` folder. I will use placeholder resource IDs in the code.
>
> [!WARNING]
> **Navigation Refactor**: Migrating to Navigation 3 is a significant change to the core of your app. This will change how screens are defined and how you pass data between them.

## Proposed Changes

### 1. Gameplay Depth: Elemental & Status Overhaul

---

#### [MODIFY] [Skill.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/models/Skill.kt)
Add `elementType` (Fire, Ice, etc.) and `statusEffect` properties.

#### [MODIFY] [Enemy.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/data/Enemy.kt)
Add `elementalResistances: Map<ElementType, Double>` to define weaknesses and strengths.

#### [MODIFY] [BattleLogic.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleLogic.kt)
Update `processTurn` to:
- Calculate damage based on elemental interactions.
- Apply and tick status effects (Burn, Poison, Freeze).
- Check for "Combos" (e.g., if the previous skill was "Stun", "Heavy Strike" deals 1.5x damage).

### 2. Bestiary & Player Progression

---

#### [MODIFY] [PlayerData.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/data/PlayerData.kt)
Add `killCounts: Map<String, Int>` to track defeated enemies.

#### [NEW] [BestiaryScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/BestiaryScreen.kt)
A new screen to view enemy stats, loot, and lore once they've been defeated enough times.

### 3. Visual Polish & "Juice"

---

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleScreen.kt)
- **Dynamic Backgrounds**: Add a background image layer that changes based on the player's current `WorldLocation`.
- **Damage Previews**: Display predicted damage ranges on skill buttons.
- **Particle System**: Implement a lightweight `ParticleSystem` for hit effects (slashes, fire, etc.).
- **Enemy Tells**: Add visual indicators (glowing eyes, aura) when an enemy is about to use a "Special" attack.

### 4. Technical Architecture: Navigation 3

---

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
Replace the manual `when` screen switching with **Jetpack Navigation 3**. This includes:
- Defining a `ScreenKey` hierarchy.
- Setting up a `NavHost` with `NavDisplay`.
- Improving backstack management.

## Verification Plan

### Automated Tests
- Run `BattleLogicTest.kt` to ensure elemental damage and status effects calculate correctly.
- Verify `PlayerData` serialization includes the new `killCounts` map.

### Manual Verification
- **Combat**: Start a battle, use a "Fire" skill against a "Grass" enemy (if defined) and check for increased damage and "Burn" status.
- **Visuals**: Enter different areas (Forest, Cave) and confirm the battle background changes.
- **Bestiary**: Defeat an enemy, then check the Bestiary to see if the kill count incremented.
- **Navigation**: Navigate through all screens to ensure Navigation 3 is handling the backstack correctly.
