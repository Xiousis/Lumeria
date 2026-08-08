# Implementation Plan - Quality of Life (QoL) Upgrades

This plan introduces several Quality of Life improvements to enhance the player experience (UI/UX, combat flow) and code maintainability (architecture).

## User Review Required

> [!IMPORTANT]
> - **Architecture Change:** I am proposing to move global states from `MainActivity` to a `MainViewModel`. This is a significant refactoring of how screens are managed.
> - **Auto-Battle Logic:** The initial implementation of Auto-Battle will simply pick the first available attack skill that the player can afford.

## Proposed Changes

### [Core Architecture]

#### [NEW] [MainViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainViewModel.kt)
- Create a ViewModel to hold `currentScreen`, `battleSeed`, `selectedStoryArc`, and other session-based states.
- Provide a unified `updatePlayer` method.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
- Inject `MainViewModel`.
- Replace the manual `when` screen switching with `AnimatedContent` for smooth transitions.
- Implement `BackHandler` to allow players to return to previous screens (e.g., from Shop back to Game Menu) using the system back button.

### [UI/UX Components]

#### [MODIFY] [RpgButton.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/RpgButton.kt)
- Integrate `LocalHapticFeedback` to provide tactile feedback on every button press.

#### [MODIFY] [MusicManager.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/utils/MusicManager.kt)
- Add a basic volume fading mechanism to make music transitions less jarring.

### [Combat & Gameplay]

#### [MODIFY] [BattleViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleViewModel.kt)
- Add `isAutoBattleActive` state.
- Implement a coroutine-based loop that automatically calls `takeTurn` when active.

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleScreen.kt)
- Add an "AUTO" toggle button in the combat interface.

#### [MODIFY] [GameMenuScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GameMenuScreen.kt)
- Add a "Quick Heal" shortcut if the player is below 50% HP and has potions in their inventory.

## Verification Plan

### Automated Tests
- Since this project lacks a robust test suite, I will focus on manual verification of state transitions and combat logic.

### Manual Verification
1. **Navigation:** Verify that switching screens (e.g., Menu -> Stats) has a fade/slide animation.
2. **Back Button:** Test that pressing the back button on the Shop screen returns the player to the Game Menu instead of closing the app.
3. **Auto-Battle:** Enter a battle, toggle "AUTO", and ensure the player takes turns without input until victory or death.
4. **Haptics:** Verify (on a physical device or supported emulator) that button clicks trigger a short vibration.
5. **Quick Heal:** Injure the player in battle, return to menu, and verify the "Quick Heal" button appears and works correctly.
