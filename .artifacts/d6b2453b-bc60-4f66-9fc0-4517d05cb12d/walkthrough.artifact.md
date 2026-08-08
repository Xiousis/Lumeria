# Walkthrough - Battle Screen HP Bars

I have added dynamic HP bars for both the hero and enemies to the battle screen.

## Changes

### [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)

- **New `HpBar` Component**:
    - Created a custom health bar that visually represents health percentage.
    - **Adaptive Coloring**: The bar smoothly transitions from **Green** (above 60%) to **Yellow** (above 25%) to **Red** (low health).
    - **Themed Design**: Features a semi-transparent gray background and rounded corners to match the game's UI style.
- **Battle Integration**:
    - **Hero HP Bar**: Added a 100.dp wide HP bar directly under Xious's health text.
    - **Enemy HP Bar**: Added a matching HP bar under the monster's health text, aligned to the right.
    - **Max HP Tracking**: Defined `playerMaxHp` to ensure the bar accurately reflects the hero's health status.

## Verification Results

### Automated Tests
- `gradle :app:assembleDebug` - **Passed**
- `render_compose_preview` - **Success** (Verified both bars appear and are correctly sized)

### Preview Result

![Battle Screen with HP Bars](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/.artifacts/d6b2453b-bc60-4f66-9fc0-4517d05cb12d/battle_hp_bars_preview.png)

> [!TIP]
> The color-coded health bars provide immediate visual feedback during combat, making it much easier for players to judge their survival chances!
