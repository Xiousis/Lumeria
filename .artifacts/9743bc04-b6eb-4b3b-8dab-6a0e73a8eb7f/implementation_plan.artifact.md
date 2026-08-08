# Implementation Plan - Buffing Non-Boss Monsters

Increase the difficulty of non-boss encounters by boosting their HP and damage scaling.

## Proposed Changes

### [Component: Logic]

#### [MODIFY] [BattleLogic.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleLogic.kt)
- **`calculateEnemyDamage`**:
    - Increase base damage values for all non-boss types (e.g., Slime: 3 -> 4, Void Reaper: 15 -> 18).
    - Increase level scaling multiplier from **1.0** to **1.5**.

### [Component: ViewModel]

#### [MODIFY] [BattleViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleViewModel.kt)
- **Scaled HP Formula**:
    - Increase base HP for different enemy types by ~20%.
    - Increase level scaling from **10** to **15** HP per level.
    - Updated formula: `scaledHp = (baseHp * 1.2).toInt() + (enemyLevel * 15)`

### [Component: Data - Database]

#### [MODIFY] [StoryEnemyDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/database/StoryEnemyDatabase.kt)
- Increase HP of all story-based non-boss enemies by ~25%.
- Example: "Training Bandit" (80 -> 100), "Void Horror" (80,000 -> 100,000).

## Verification Plan

### Automated Tests
- Run `:app:testDebugUnitTest` to ensure battle logic remains stable.

### Manual Verification
- Start a grinding battle in "Training Fields" and "Goblin Forest".
- Verify that enemies have higher HP than before.
- Verify that enemies deal slightly more damage to Xious.
- Progress through a Story event and verify the fixed-stat enemies are also tougher.
