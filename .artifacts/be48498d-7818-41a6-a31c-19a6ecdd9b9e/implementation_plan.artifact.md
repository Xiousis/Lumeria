# Implementation Plan - UI Refactoring and Bug Fixes

This plan addresses three main issues:
1. Moving the Journal out of "Story Mode" and into the Main Menu.
2. Fixing a categorization error in the Journal where an achievement was listed under "Worlds".
3. Refactoring the "New Game" and "Continue" logic to be more intuitive and robust.

## Proposed Changes

### 1. Journal Refactoring

- **[MODIFY] [MainMenu.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/MainMenu.kt)**
    - Add `onJournal` callback to the `MainMenu` composable.
    - Add a "Journal" button to the menu layout.
    - Pass `playerData` to check if the Journal should be enabled (optional, but probably better if it's always accessible to see progress).

- **[MODIFY] [StorySelectionScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/StorySelectionScreen.kt)**
    - Remove the "Journal" button to declutter the "Story Mode" specific UI as requested.

- **[MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)**
    - Update `MainMenu` call site to handle `onJournal` (switching to `Screen.StoryJournal`).
    - Adjust `Screen.StoryJournal` navigation so it can return to the correct screen (Main Menu or Game Menu).

### 2. Achievement Categorization Fix

- **[MODIFY] [StoryCodexDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/database/StoryCodexDatabase.kt)**
    - Remove "The Immortal Legend" from the `entries` list.

- **[MODIFY] [AchievementDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/database/AchievementDatabase.kt)**
    - Add "The Immortal Legend" achievement.
    ```kotlin
    Achievement(
        id = "immortal_legend",
        title = "The Immortal Legend",
        description = "Reach the absolute peak of mortal potential (Level 50).",
        icon = "🌟"
    )
    ```

- **[MODIFY] [AchievementManager.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/utils/AchievementManager.kt)**
    - Add logic to check for "immortal_legend" (e.g., `playerData.level >= 50`).

### 3. New Game / Continue Logic Refinement

- **[MODIFY] [MainMenu.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/MainMenu.kt)**
    - Accept `playerData` as a parameter.
    - Disable "Continue" button if `!playerData.introSeen`.
    - Ensure "New Game" button is prominent.

- **[MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)**
    - Change `TitleScreen` to always navigate to `MainMenu` first.
    - In `MainMenu`'s `onStartGame` (Continue), navigate to `GameMenu`.
    - In `MainMenu`'s `onNewGame`, navigate to `Story`.
    - In `StoryScreen`'s callback, set `introSeen = true` and navigate directly to `GameMenu` (to start the adventure immediately after the intro).

## Verification Plan

### Manual Verification
1. **New Game Flow**: Open app -> Title -> Main Menu. Verify "Continue" is disabled. Click "New Game" -> Story -> Click "Begin Adventure" -> Game Menu.
2. **Continue Flow**: Quit and reopen. Verify "Continue" is now enabled. Click "Continue" -> Game Menu.
3. **Journal Access**: Verify Journal button exists on Main Menu and Game Menu, but NOT on Story Selection screen.
4. **Achievement Fix**: Open Journal -> Codex -> World. Verify "The Immortal Legend" is gone. Open Achievements tab. Verify "The Immortal Legend" is present.
