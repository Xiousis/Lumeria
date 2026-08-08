# Enemy Level and HP Scaling Fix Plan

The goal is to ensure that normal enemies in world map zones have levels and HP that match the zone's difficulty, independent of the player's current level.

## User Review Required

> [!IMPORTANT]
> Enemies will no longer scale down to the player's level. Entering a high-level zone at a low level will be extremely dangerous as enemies will be at their natural zone levels.

## Proposed Changes

### Battle System

#### [MODIFY] [BattleViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleViewModel.kt)
- **Level Scaling Fix**: Instead of tracking the player's level, enemies will now pick a random base level within the zone's `[minLevel, maxLevel]` range.
- **Difficulty Offset**: Apply the `difficultyOffset` to this randomized base level to differentiate between weak and strong mobs within the same zone.
- **HP Scaling Fix**: Update the `scaledHp` calculation to use the newly calculated `enemyLevel` instead of `playerData.level`.
- **Base HP Adjustments**: Slightly increase base HP for late-game mobs to ensure they feel like a threat.

#### [MODIFY] [BattleLogic.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleLogic.kt)
- **XP Reward Adjustment**: Update the XP formula to provide better scaling in higher level zones. New formula: `(enemy.level * 15) + 25`.
- **Gold Reward Consistency**: Since `enemy.maxHp` will now be correctly scaled, the `enemy.maxHp / 2` formula will naturally provide more gold in higher zones.

## Verification Plan

### Automated Tests
- Build the project to ensure no regression.

### Manual Verification
- **Level Consistency**: Enter "Goblin Forest" (Lv 4-6) at Lv 1. Verify enemies are between Lv 4-6.
- **HP Consistency**: Verify a Lv 4 Goblin has significantly more HP than a Lv 1 Slime, regardless of the player's level.
- **Rewards**: Defeat a Lv 4 Goblin and verify it gives more XP than a Lv 1 Slime.
