# Walkthrough: Gambling House & Blackjack

I have successfully implemented a Gambling House in the Market section of the game menu. Players can now test their luck and double their gold through a game of Blackjack.

## Changes Made

### UI & Navigation
- **New Screen**: Added [GamblingHouseScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GamblingHouseScreen.kt) which contains the full Blackjack implementation.
- **Market Tab**: Updated the [GameMenuScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/screens/GameMenuScreen.kt) to include a card for "THE GAMBLING HOUSE" under the Market tab.
- **Navigation**: Updated [Screen.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/models/Screen.kt) and [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/MainActivity.kt) to support the new screen.

### Blackjack Logic
- **Deck Management**: Implemented a standard 52-card deck with automated shuffling.
- **Game Phases**:
    - **Betting**: Players can adjust their bet in increments of 10 gold.
    - **Player Turn**: Options to "Hit" (draw a card) or "Stand" (end turn).
    - **Dealer Turn**: Automated logic where the dealer hits until they reach a hand value of at least 17.
    - **Aces**: Hand value calculation automatically optimizes Aces (1 or 11) to prevent the player or dealer from busting.
- **Payouts**:
    - Standard wins pay out 1:1.
    - **Blackjack** (21 on the first two cards) pays out 3:2.
    - **Push**: If the player and dealer tie, the bet is returned.

## Meet Frank, Your Host
- **NPC Introduction**: The Gambling House is now hosted by **Frank**.
- **Visuals**: Added `frank.png` as the immersive background and a portrait for Frank.
- **Interactive Dialogue**: Frank now speaks to you! He'll greet you, react to your wins/losses, and comment on the game flow (e.g., "BLACKJACK! Unbelievable luck!").
- **UI Enhancement**: The dialogue box is styled to match the RPG aesthetic with a semi-transparent black background and cyan accents.

### Automated Tests
- Ran `gradle build` to ensure all screens and navigation points are correctly linked. The build was **successful**.

### Previews
- Updated [Previews.kt](file:///C:/Users/buggm/AndroidStudioProjects/TEXTBASEDRPGMAGIC/app/src/main/java/com/example/textbasedrpgmagic/ui/Previews.kt) with a new preview for the Gambling House to verify the UI layout.

> [!TIP]
> Use the Gambling House to quickly increase your gold before visiting Billy's Store for new gear, but be careful—the house always has a slight edge!
