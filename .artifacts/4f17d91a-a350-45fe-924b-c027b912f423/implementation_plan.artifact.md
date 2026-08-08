# QoL and Game Suggestions Implementation Plan

This plan outlines several Quality of Life (QoL) improvements and a new game feature to enhance the player experience in "TEXT BASED RPG MAGIC".

## Proposed Changes

### 1. Quality of Life (QoL) Improvements

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleScreen.kt)
- **Auto-Battle Toggle**: Add a "AUTO" toggle button to the `BattleScreen`. When enabled, the game will automatically select the first available (ready and affordable) attack skill for the player.
- **Battle Speed Toggle**: Add a "FAST" toggle to increase the speed of animations and log entries.

#### [MODIFY] [BattleViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleViewModel.kt)
- Add logic to handle auto-battle turns.
- Implement a delay adjustment based on the "FAST" toggle.

#### [MODIFY] [GameMenuScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GameMenuScreen.kt)
- **Quick Rest**: Add a "REST AT INN" option to the `MarketTab`.
- This will fully restore HP and Mana for a gold cost (e.g., `level * 5`).

### 2. New Game Feature: The Arena

#### [NEW] [ArenaScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ArenaScreen.kt)
- A new screen where players can participate in "The Arena".
- The Arena consists of continuous waves of enemies.
- After every 5 waves, the player gets a choice to "Cash Out" or "Continue" for better rewards.
- Failing a wave results in losing half the accumulated Arena rewards.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
- Add `Screen.Arena` to the navigation.
- Update `GameMenuScreen` to include an "ARENA" button in the `AdventureTab`.

### 3. Stat Comparison Polish

#### [MODIFY] [ShopItemRow.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/ShopItemRow.kt)
#### [MODIFY] [InventoryItemRow.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/InventoryItemRow.kt)
- Ensure stat differences are clearly color-coded (Green for positive, Red for negative) and shown for all stats (STR, INT, AGI, etc.).

## Verification Plan

### Automated Tests
- N/A (Mostly UI and state logic)

### Manual Verification
- **Quick Rest**: Verify that gold is deducted and HP/Mana are restored.
- **Auto-Battle**: Verify that the game takes turns automatically when toggled on.
- **Arena**: Verify wave progression and reward cashing out.
- **Stat Comparison**: Check if comparisons are accurate and visible in both Shop and Inventory.
