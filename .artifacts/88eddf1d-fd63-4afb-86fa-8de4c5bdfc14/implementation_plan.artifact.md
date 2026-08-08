# Project Improvement Plan: TEXT BASED RPG MAGIC

This plan aims to improve the maintainability, scalability, and code quality of the RPG project. The focus is on refactoring the monolithic navigation and battle systems, and ensuring proper ViewModel management.

## User Review Required

> [!IMPORTANT]
> This refactor involves significant changes to `MainActivity.kt`, `BattleViewModel.kt`, and `BattleScreen.kt`. While the functionality will remain the same, the internal structure will change to follow modern Android best practices.

## Proposed Changes

### 1. Navigation & Architecture Refactor
Currently, `MainActivity` handles all navigation logic in a single `when` block within `setContent`. We will move this to a dedicated `AppNavigation` composable and ensure ViewModels are properly scoped.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
- Clean up `onCreate` and move the navigation `when` block into a separate function or composable.
- Ensure `BattleViewModel` is not instantiated using `remember` but via a proper factory or `viewModel()` call.

#### [NEW] [AppNavigation.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/ui/AppNavigation.kt)
- Create a central hub for screen transitions.

### 2. Battle System Refactoring
The `BattleViewModel` (789 lines) and `BattleScreen` (814 lines) are currently "God Objects". We will break them down.

#### [NEW] [BattleUiState.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleUiState.kt)
- Consolidate the ~30 individual state variables in `BattleViewModel` into a single, cohesive state object.

#### [NEW] [BattleEngine.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleEngine.kt)
- Move pure game logic (damage calculations, turn processing, status effect handling) from the ViewModel to this engine.

#### [MODIFY] [BattleViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleViewModel.kt)
- Delegate logic to `BattleEngine`.
- Expose a single `StateFlow<BattleUiState>`.

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleScreen.kt)
- Extract sub-composables (e.g., `BattleLog`, `PlayerStats`, `EnemyStats`, `ActionButtons`) into separate files or functions.

### 3. Business Logic Cleanup
Move game-wide logic (like death penalties and progression calculations) out of UI/Navigation layers and into the ViewModels.

#### [MODIFY] [MainViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainViewModel.kt)
- Centralize logic for gold loss on death and arc progression.

## Verification Plan

### Automated Tests
- Run existing `BattleLogicTest.kt` to ensure core mechanics are not broken.
- Add unit tests for the new `BattleEngine`.

### Manual Verification
- Verify all screen transitions work as expected.
- Test a full battle loop (attack, use item, victory, defeat).
- Verify gold loss on death and XP gain on victory.
