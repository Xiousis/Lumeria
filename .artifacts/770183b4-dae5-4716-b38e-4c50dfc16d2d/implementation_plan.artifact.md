# Implementation Plan - Easier Betting at the Casino

The goal is to improve the user experience in the `GamblingHouseScreen` by making it faster and easier to place larger bets. Currently, users can only increment/decrement by 10.

## User Review Required

> [!NOTE]
> I will be adding buttons for `+50`, `+100`, `MIN` (10), and `MAX` (current gold) to the betting interface.

## Proposed Changes

### UI Components

#### [MODIFY] [GamblingHouseScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GamblingHouseScreen.kt)
- Add new buttons for betting increments: `+50` and `+100`.
- Add `MIN` and `MAX` buttons for quick selection.
- Refactor the betting controls layout to accommodate these new buttons while keeping it clean and readable.
- Ensure the `MAX` button correctly accounts for the player's total gold.
- Ensure that the bet cannot exceed the player's gold when using increments.

## Verification Plan

### Manual Verification
1.  Navigate to the Gambling House in the app.
2.  Verify that the new buttons (+50, +100, MIN, MAX) are visible in the betting phase.
3.  Test each button:
    -   `+50` and `+100` should increase the bet by the respective amount, capped by total gold.
    -   `MIN` should reset the bet to 10.
    -   `MAX` should set the bet to the player's current gold.
4.  Ensure the "Deal" button is correctly enabled/disabled based on the bet and gold.
5.  Play a game to ensure the logic still works as expected with the new bet values.
