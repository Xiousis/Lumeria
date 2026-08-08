# Implementation Plan - Fix Compose Preview Render Issue

The Compose Preview for `TitleScreen` is crashing because `lumeria_logo.xml` is an empty `<selector>`, which `painterResource` cannot load.

## User Review Required

> [!IMPORTANT]
> I will replace the empty `lumeria_logo.xml` with a placeholder vector drawable. If you have a specific logo asset, you should replace this file with your actual logo later.

## Proposed Changes

### [app](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app)

#### [MODIFY] [lumeria_logo.xml](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/res/drawable/lumeria_logo.xml)
- Replace the empty `<selector>` with a valid `<vector>` drawable representing a placeholder logo.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
- (Optional) I might improve how the resource is loaded to be more idiomatic, but the primary fix is the resource itself. I'll stick to fixing the resource first as it's the direct cause of the crash.

## Verification Plan

### Manual Verification
- Render the `TitlePreview` in Android Studio to ensure it no longer crashes and shows the placeholder logo.
