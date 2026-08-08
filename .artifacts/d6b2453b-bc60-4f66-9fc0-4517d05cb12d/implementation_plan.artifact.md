# Implementation Plan - Add HP Bars to Battle Screen

Add visual HP bars for both the hero (Xious) and the enemies to provide a clear indicator of remaining health during battle.

## Proposed Changes

### [Component: UI]

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)

- **New `HpBar` Composable**: Create a reusable `HpBar` that takes `current` and `max` values. It will feature:
    - A themed background.
    - A smooth color transition (Green for high HP, Yellow for mid, Red for low).
    - Rounded corners.
- **Update `BattleScreen`**:
    - Hero Section: Add the `HpBar` below the "Xious HP" text.
    - Enemy Section: Add the `HpBar` below the "HP" text for the enemy.
    - Ensure the player's max HP is defined (currently 30).

## Verification Plan

### Automated Tests
- Run `gradle :app:assembleDebug` to ensure compilation.
- Use `render_compose_preview` for `BattlePreview` to verify the HP bars' appearance and responsiveness to HP values.

### Manual Verification
- Test combat actions (Slash, Heal, etc.) to ensure the HP bars animate/update correctly as health changes.
