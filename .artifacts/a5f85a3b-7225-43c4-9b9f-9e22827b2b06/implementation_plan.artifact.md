# Fix Unresolved Resource References and Navigation Errors

The build is failing due to missing `R` class imports in multiple files across the project. Additionally, there is a property access error in `AppNavigation.kt`.

## User Review Required

> [!NOTE]
> Most changes involve adding a missing import (`import com.example.lumeria.R`) to files that use Android resources.

## Proposed Changes

### Battle Module

#### [MODIFY] [BattleComponents.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/battle/BattleComponents.kt)
- Add `import com.example.lumeria.R`.
- Import `PlayerBuff`, `BuffType`, and `ElementType` from `com.example.lumeria.models` to replace fully qualified names.

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/battle/BattleScreen.kt)
- Add `import com.example.lumeria.R`.

### Components Module

#### [MODIFY] [QuestRow.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/components/QuestRow.kt)
- Add `import com.example.lumeria.R`.

#### [MODIFY] [RpgButton.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/components/RpgButton.kt)
- Add `import com.example.lumeria.R`.

### Screens Module

#### [MODIFY] Multiple Files in `screens/`
- Add `import com.example.lumeria.R` to the following files:
  - `ArcCompletionScreen.kt`
  - `ArenaSelectionScreen.kt`
  - `BankScreen.kt`
  - `BestiaryScreen.kt`
  - `BlacksmithScreen.kt`
  - `BountyBoardScreen.kt`
  - `ElderRitualScreen.kt`
  - `FishingScreen.kt`
  - `GamblingHouseScreen.kt`
  - `GameMenuScreen.kt`
  - `InnScreen.kt`
  - `InventoryScreen.kt`
  - `KingdomScreen.kt`
  - `MainMenu.kt`
  - `SettingsScreen.kt`
  - `SkillScreen.kt`
  - `StatsScreen.kt`
  - `StoryDialogueScreen.kt`
  - `StoryJournalScreen.kt`
  - `StoryScreen.kt`
  - `StorySelectionScreen.kt`
  - `TrophyRoomScreen.kt`
  - `WorldMapScreen.kt`
  - `YouDiedScreen.kt`

### UI Module

#### [MODIFY] [AppNavigation.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/ui/AppNavigation.kt)
- Add `import com.example.lumeria.R`.
- Fix unresolved reference by changing `battleViewModel.victoryProcessed` to `battleViewModel.state.value.victoryProcessed`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify all build errors are resolved.
