# Implementation Plan: Gambling House & Blackjack

Add a new "Gambling House" location to the market where players can bet gold on a simple game of Blackjack.

## Proposed Changes

### [Models]

#### [MODIFY] [Screen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/models/Screen.kt)
- Add `GamblingHouse` to the `Screen` enum.

### [UI / Screens]

#### [MODIFY] [GameMenuScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GameMenuScreen.kt)
- Update `GameMenuScreen` signature to accept `onGambling: () -> Unit`.
- Update `MarketTab` to accept `onGambling: () -> Unit`.
- Add a `MenuCard` for "THE GAMBLING HOUSE" in `MarketTab`.

#### [NEW] [GamblingHouseScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GamblingHouseScreen.kt)
- Create a new Composable for the Blackjack game.
- Implement game state:
    - `deck`: List of cards (Rank + Suit).
    - `playerHand`: List of cards.
    - `dealerHand`: List of cards.
    - `currentBet`: Int.
    - `gameStatus`: Enum (BETTING, PLAYER_TURN, DEALER_TURN, ENDED).
    - `resultMessage`: String.
- Implement Blackjack logic:
    - Value calculation (Aces = 1 or 11).
    - Dealing (2 cards each).
    - Player actions: Hit, Stand.
    - Dealer AI: Hit until total is 17 or more.
    - Payouts: 1:1 for win, 3:2 for Blackjack (21 on first 2 cards).

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt)
- Add `Screen.GamblingHouse` to the `when` block in `setContent`.
- Pass `onGambling` lambda to `GameMenuScreen`.

## Verification Plan

### Manual Verification
- Navigate to the Market tab in the Game Menu.
- Confirm "THE GAMBLING HOUSE" is visible.
- Enter the Gambling House.
- Test betting logic (ensure player can't bet more than they have).
- Play several rounds of Blackjack:
    - Verify hand values (especially Aces).
    - Verify "Hit" and "Stand" functionality.
    - Verify Dealer AI behavior.
    - Verify win/loss/tie outcomes and gold updates.
- Return to Game Menu and ensure gold balance is correctly reflected.
