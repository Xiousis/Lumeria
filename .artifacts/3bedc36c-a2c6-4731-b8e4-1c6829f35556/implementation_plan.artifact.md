# Implementation Plan - Game Polish and "Feel" Enhancements

This plan aims to elevate the visual quality and interactive feedback of "TEXT BASED RPG MAGIC" through theme updates, animations, and haptic feedback.

## Proposed Changes

### 1. Theme & Visual Identity

#### [MODIFY] [Color.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/theme/Color.kt)
- Replace default Material colors with a "Fantasy/RPG" palette:
    - `RpgGold`: #FFD700
    - `RpgCrimson`: #8B0000
    - `RpgSlate`: #2F4F4F
    - `RpgDeepBlack`: #121212
    - `RpgCyan`: #00FFFF

#### [MODIFY] [Type.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/theme/Type.kt)
- Adjust typography tracking and line heights to feel more cinematic.

#### [MODIFY] [Theme.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/theme/Theme.kt)
- Apply the new `DarkColorScheme` as the primary theme.

---

### 2. UI Component Polish

#### [MODIFY] [ManaBar.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/ManaBar.kt)
- Add `animateFloatAsState` to smooth out mana changes, similar to `HpBar`.

#### [NEW] [RpgButton.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/RpgButton.kt)
- Create a reusable button with a custom border, gradient background, and a "press" scale animation.

#### [MODIFY] [GameMenuScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GameMenuScreen.kt)
- Use `RpgButton` for the main actions.
- Add a slight scale animation to `MenuCard` when clicked.

---

### 3. Battle Experience (The "Feel")

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/BattleScreen.kt)
- Use `AnimatedVisibility` for **Victory Overlay**, **Item Menu**, and **Skill Tooltips**.
- Add a screen shake effect when taking heavy damage.

#### [MODIFY] [BattleViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleViewModel.kt)
- Add a `triggerHaptic` callback or state that the UI can listen to for performing haptic feedback on hits and crits.

---

### 4. Interactive Feedback

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
- Add simple click sound effects using `SoundPool` or `MediaPlayer` (if resources allow).

## User Review Required

> [!NOTE]
> I will be updating the core color scheme to a "Fantasy Dark" style. This will change the look of all screens to be more consistent with the RPG genre.

## Verification Plan

### Automated Tests
- Build verification to ensure new components are correctly integrated.

### Manual Verification
- **Visuals**: Observe the new color scheme and typography.
- **Animations**: Watch the HP/Mana bars during battle to ensure smooth transitions.
- **Overlays**: Check if the Victory screen fades in smoothly.
- **Interaction**: Verify the scale animation on buttons when pressed.
