# Implementation Plan - Custom ProgressBar for HP/Mana Bars

The current HP and Mana bars are implemented using custom Compose logic with hardcoded padding percentages to align fills within art frames. This approach can be fragile and lead to scaling issues across different device sizes.

This plan migrates the implementation to use a standard Android `ProgressBar` with a custom `ProgressDrawable`. This allows Android's native rendering to handle the clipping and scaling of the artwork correctly.

## User Review Required

> [!IMPORTANT]
> This change switches from a pure Compose implementation to an `AndroidView` wrapper. While this provides more robust scaling, it might behave slightly differently in terms of layout constraints if the parent container doesn't provide enough space.

## Proposed Changes

### Resources & Drawables

#### [NEW] [hp_progress.xml](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/res/drawable/hp_progress.xml)
Create the progress drawable for the Hero HP bar as suggested. I will use a layer-list that includes the background, the progress fill, and the artwork frame.

#### [NEW] [enemy_hp_progress.xml](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/res/drawable/enemy_hp_progress.xml)
Create a similar drawable for the Enemy HP bar, likely using a red-themed fill or the enemy-specific artwork.

#### [NEW] [hero_mana_progress.xml](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/res/drawable/hero_mana_progress.xml)
Create a progress drawable for the Hero Mana bar using blue-themed colors.

---

### UI Components

#### [MODIFY] [HpBar.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/components/HpBar.kt)
- Update `HeroHpBar` and `EnemyHpBar` to use `AndroidView`.
- Wrap a `ProgressBar` with the corresponding custom `progressDrawable`.
- Maintain the `animateFloatAsState` logic to ensure smooth transitions between health values.

#### [MODIFY] [ManaBar.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/components/ManaBar.kt)
- Update `HeroManaBar` and `StandardManaBar` to follow the same pattern as `HpBar`.

## Verification Plan

### Automated Tests
- I will run `render_compose_preview` for the `BarsPreview` in `Previews.kt` to ensure the bars render correctly and match the desired artwork.

### Manual Verification
- Deploy the app and check the battle screen to ensure the HP bars update smoothly during combat.
- Check the character stats or game menu to see if the mana bars are correctly scaled.
