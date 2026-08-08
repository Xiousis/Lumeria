# Walkthrough - Buffing Non-Boss Monsters

I have increased the difficulty of non-boss encounters to provide a more challenging experience for Xious.

## Changes

### Enemy Damage Buffs
- **Base Damage**: Increased the base damage for all standard enemy types (e.g., Slime: 3 -> 4, Void Reaper: 15 -> 20).
- **Scaling**: Improved the level-based damage multiplier from **1.0 to 1.5**. Enemies will now hit significantly harder as their level increases.

### Enemy HP Buffs
- **Random Encounters**:
    - Increased base HP for all random enemies by approximately **20%**.
    - Increased the HP gain per level from **10 to 15**.
- **Story Enemies**:
    - Increased the fixed HP stats of all story-based non-boss enemies by **25%**.
    - Example: "Void Horror" HP increased from 80,000 to **100,000**.

### Technical Implementation
- Updated `BattleLogic.calculateEnemyDamage` with new damage constants and scaling.
- Updated `BattleViewModel` random encounter generation logic with new HP constants and scaling.
- Updated `StoryEnemyDatabase.kt` with a full rebalance of all 24 story-based enemies.

## Verification Results

### Automated Tests
- Ran `:app:testDebugUnitTest`. Result: **6 passed, 0 failed**.
- Verified that basic damage and HP calculations remain functional with the new constants.

### Manual Verification
- Verified that random encounters now spawn with higher HP (e.g., a Level 1 Slime now has ~27 HP instead of 20).
- Verified that enemy damage output is higher across different level ranges.
