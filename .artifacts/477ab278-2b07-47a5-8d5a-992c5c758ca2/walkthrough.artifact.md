# Walkthrough - Added Head Gear and Off-Hand Gear Slots

I have added a dedicated **Head Gear** slot and a **second Off-Hand Gear** slot to the game. I also ensured that **Boots** are properly integrated into all stats calculations and UI screens.

## Changes Made

### Core Systems
- **GameDatabase**:
    - Added `HeadGear` data class.
    - Added `headGears` list with new items like "Leather Cap", "Iron Helm", and "Knight Helm".
    - Migrated high-level crowns and helms (e.g., "Helm of the First Hero", "Royal Goblin Crown") from the Armor category to the Head Gear category.
- **PlayerData**:
    - Added `equippedHead` and `equippedOffHand2` fields to the player data model.
- **Battle Logic**:
    - Updated defense calculations to include the `Head` slot.
    - Updated damage and stat calculations to include bonuses from **both** Off-Hand slots.

### UI Enhancements
- **Inventory Screen**:
    - Added "Head" and "Off-Hand 2" rows to the "Current Loadout" panel.
    - Added a new "Head Gear" section to the inventory list.
    - Updated the "Off-Hands" section to allow equipping items to either Slot 1 or Slot 2.
    - Total stats (ATK, DEF, AGI) are now calculated using all equipment slots.
- **Shop Screen**:
    - Added a "Head Gear" category to the shop.
    - Updated the "Sell" tab to support selling head gear and ensure items in the new slots are protected from accidental sale.

## Verification Results

### Loadout Verification
The inventory loadout now displays all 7 equipment slots:
1. Weapon
2. Head (+DEF)
3. Armor (+DEF)
4. Boots (+AGI)
5. Shield (+DEF)
6. Off-Hand 1 (Variable Stats)
7. Off-Hand 2 (Variable Stats)

### Stat Calculation
- Equipping a piece of Head Gear correctly increases the Total DEF.
- Equipping items in both Off-Hand slots correctly sums their respective STR, AGI, and INT bonuses.
- Boots bonuses are correctly included in the Total AGI.

> [!TIP]
> You can now visit **Billy's General Store** to pick up some starting head gear like the "Leather Cap" or "Iron Helm"!
