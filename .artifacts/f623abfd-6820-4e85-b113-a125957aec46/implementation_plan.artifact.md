# Crop AI Artifacts from Background Images

The goal is to apply a slight zoom to full-screen background images across various screens to hide potential "AI generated" text or artifacts that often appear in corners.

## User Review Required

> [!NOTE]
> I will apply a `1.1f` (10%) zoom factor to the background images of the following screens. This will slightly crop the edges, ensuring that any corner text is hidden while maintaining the composition of the art.
>
> Targeted Screens:
> - `TitleScreen`
> - `MainMenu`
> - `ShopScreen`
> - `InnScreen`
> - `StoryDialogueScreen`
> - `ArcCompletionScreen`
> - `WorldMapScreen`
> - `GamblingHouseScreen`
> - `ElderRitualScreen`

## Proposed Changes

### UI Components

I will add `.graphicsLayer(scaleX = 1.1f, scaleY = 1.1f)` to the `Modifier` of the primary background `Image` in each of the following files.

#### [MODIFY] [TitleScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/TitleScreen.kt)
#### [MODIFY] [MainMenu.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/MainMenu.kt)
#### [MODIFY] [ShopScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ShopScreen.kt)
#### [MODIFY] [InnScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/InnScreen.kt)
#### [MODIFY] [StoryDialogueScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/StoryDialogueScreen.kt)
#### [MODIFY] [ArcCompletionScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ArcCompletionScreen.kt)
#### [MODIFY] [WorldMapScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/WorldMapScreen.kt)
#### [MODIFY] [GamblingHouseScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GamblingHouseScreen.kt)
#### [MODIFY] [ElderRitualScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/ElderRitualScreen.kt)

## Verification Plan

### Automated Tests
- I will verify that the `graphicsLayer` modifier with `scaleX = 1.1f` and `scaleY = 1.1f` is present in all targeted files.

### Manual Verification
- Deploy the app and navigate through all major screens (Title, Shop, Map, Inn, Elder, etc.).
- Verify that the background images fill the screen correctly without showing any unwanted corner text.
- Ensure that no critical part of the artwork is lost due to the 10% zoom.
