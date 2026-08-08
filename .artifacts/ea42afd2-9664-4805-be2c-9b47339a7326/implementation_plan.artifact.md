# Implementation Plan - Brighten UI Buttons

This plan addresses the user's feedback that some buttons are too dark by brightening the color palette for buttons and interactive elements across the app.

## User Review Required

> [!NOTE]
> I will be using colors from the existing `Color.kt` or slightly modified versions to maintain the RPG theme while improving visibility.

## Proposed Changes

### Theme & Components

#### [MODIFY] [Color.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/theme/Color.kt)
- Add brighter versions of Slate and Gray for better contrast.
- Define specific button colors for different skill types (Attack, Support, Buff).

#### [MODIFY] [RpgButton.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/components/RpgButton.kt)
- Update default `containerColor` to a brighter themed color.

### Screen-Specific Brightening

#### [MODIFY] [BattleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/battle/BattleScreen.kt)
- Brighten the colors for skill types (Attack, Support, Buff) in the skill grid.

#### [MODIFY] [MainMenu.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/MainMenu.kt)
- Update "New Game" and "Journal" buttons to use brighter, themed colors.

#### [MODIFY] [GameMenuScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GameMenuScreen.kt)
- Brighten "Return to Title" button.
- Improve contrast for `MenuCard` backgrounds if necessary.

#### [MODIFY] [ShopScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ShopScreen.kt)
- Update "Return" button and Tab colors.

#### [MODIFY] [StatsScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/StatsScreen.kt)
- Update "Return" button.

#### [MODIFY] [InventoryScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/InventoryScreen.kt)
- Update "Return" button.

#### [MODIFY] [SkillScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/SkillScreen.kt)
- Update "Return" button and unselected slot backgrounds.

## Verification Plan

### Automated Tests
- Not applicable for UI color changes, but I will ensure the code still compiles and renders correctly.

### Manual Verification
- Render Compose Previews for the affected screens.
- Deploy to an emulator to verify visibility in various screens.
