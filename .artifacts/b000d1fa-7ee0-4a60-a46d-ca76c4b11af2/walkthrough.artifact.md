# Walkthrough - Rarity Visuals & Location Unlocking Fixes

I have overhauled the rarity color system and fixed a critical bug where items and missions weren't appearing correctly when new zones were unlocked.

## Changes Made

### Centralized Rarity System
- **New Colors**:
    - **Legendary**: Gold (`0xFFFFD700`)
    - **Mythic**: Pink (`0xFFFF69B4`)
    - **God Tier**: Light Purple (`0xFFCE93D8`)
- **Special Indicator**: Added a **Purple Star (⭐)** next to the name of all **God Tier** items and traits.
- **RarityUtils**: Created a centralized utility to ensure these colors and icons are identical across all screens (Battle, Shop, Inventory, Stats, and Elder's Hut).

### Progression & Unlocking
- **Unified Unlocking Logic**: The **Shop** and **Quest Log** now use the same logic as the World Map.
    - Items and missions for a specific zone will now correctly appear once you reach the **Required Level** for that zone (or unlock it via story).
    - This fixes the issue where players would unlock a zone on the map but see an empty shop or mission list.

### UI Consistency
- **Battle Loot**: Drops on the victory screen now show the new colors and the purple star for God Tier items.
*   **Inventory & Stats**: Your equipment and permanent traits now reflect the new visual style.
*   **Shop Tabs**: Both "Buy" and "Sell" tabs are updated. "God Tier" items in the Sell tab are now explicitly labeled "CANNOT BE SOLD" with the button removed for safety.

## Verification
- **Verified**: Items in "Goblin Forest" (Level 4) now correctly appear in Billy's Shop once the player hits Level 4.
- **Verified**: "God Tier" items (like Starshard Edge) now display in light purple with a purple star.
- **Verified**: New missions appear in the Quest Log as soon as their respective zones are unlocked by level.

> [!TIP]
> Check out the **Stats Screen** to see your permanent traits with their updated rarity colors and stars!
