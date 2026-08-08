# Implementation Plan - Rarity Visual Enhancements

The goal is to update the visual representation of item and trait rarities across the game to match the new color scheme and add special indicators for "God Tier" items.

## Proposed Changes

### Utilities & Theme

#### [NEW] [RarityUtils.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/utils/RarityUtils.kt)
- Create a central utility to manage rarity colors and symbols.
- **Colors**:
    - Uncommon: Green (`0xFF4CAF50`)
    - Rare: Blue (`0xFF2196F3`)
    - Epic: Deep Purple (`0xFFE040FB`)
    - **Legendary**: Gold (`0xFFFFD700`)
    - **Mythic**: Pink (`0xFFFF69B4`)
    - **God Tier**: Light Purple (`0xFFCE93D8`)
- **Symbol**:
    - God Tier: Purple Star (`⭐` with `0xFF9C27B0` color)

### UI Components

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleScreen.kt)
- Update the victory loot list to use `RarityUtils`.
- Use a `Row` to display "God Tier" items with the purple star.

#### [MODIFY] [InventoryItemRow.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/InventoryItemRow.kt)
- Update to use `RarityUtils` for colors.
- Add the purple star for "God Tier" items.

#### [MODIFY] [SellItemRow.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/SellItemRow.kt)
- Update to use `RarityUtils` for colors.
- Add the purple star for "God Tier" items.

#### [MODIFY] [ElderRitualScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ElderRitualScreen.kt)
- Update trait display logic to use `RarityUtils` for consistent colors.
- Add the purple star to "God Tier" traits in both the list and the reveal popup.

#### [MODIFY] [ShopScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ShopScreen.kt)
- Ensure consistent rarity colors in the "Buy" tab item rows.

## Verification Plan

### Manual Verification
1. **Victory Screen**: Defeat a boss and verify the loot color matches the new rarity scheme.
2. **Inventory**: Check the loadout and item list; verify "God Tier" items have a light purple color and a purple star.
3. **Shop**: Verify items in Billy's Shop (Buy and Sell tabs) use the new colors.
4. **Elder's Hut**: Roll a trait and verify the rarity color (especially God Tier/Mythic/Legendary) matches the new request.
