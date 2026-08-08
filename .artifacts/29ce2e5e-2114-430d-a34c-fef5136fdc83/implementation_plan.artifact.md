# Implementation Plan - Google Play Billing Integration for Donations

The goal is to replace the external "Buy me a coffee" link with a compliant Google Play Billing integration using consumable In-App Purchases (IAP).

## User Review Required

> [!IMPORTANT]
> You must create the following products in the Google Play Console as **Consumable In-App Purchases**:
> - `donation_coffee_small` (e.g., "Small Coffee")
> - `donation_coffee_medium` (e.g., "Regular Coffee")
> - `donation_coffee_large` (e.g., "Large Coffee")
>
> The code will use these IDs to fetch prices and launch the purchase flow.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/gradle/libs.versions.toml)
- Add `billing` version and library definition.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/build.gradle.kts)
- Add the billing library dependency.

### Core Logic

#### [NEW] [BillingManager.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/billing/BillingManager.kt)
- Create a class to encapsulate Google Play Billing logic (initializing `BillingClient`, fetching products, handling purchases, and acknowledging/consuming them).

#### [NEW] [BillingViewModel.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/viewmodels/BillingViewModel.kt)
- Create a ViewModel to expose billing state (product details, purchase status) to the UI.

### UI Components

#### [NEW] [DonationDialog.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/components/DonationDialog.kt)
- A dialog that shows the available donation tiers with their localized prices fetched from Google Play.

#### [MODIFY] [SettingsScreen.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/screens/SettingsScreen.kt)
- Replace the `uriHandler.openUri` call with a call to show the `DonationDialog`.

### Integration

#### [MODIFY] [MainActivity.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/MainActivity.kt)
- Initialize `BillingViewModel` and pass it down to `AppNavigation`.

#### [MODIFY] [AppNavigation.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/example/lumeria/ui/AppNavigation.kt)
- Wire up the `BillingViewModel` to the `SettingsScreen`.

## Verification Plan

### Automated Tests
- I'll add unit tests for `BillingViewModel` to ensure it correctly maps billing states.

### Manual Verification
1.  **Build and Deploy**: Ensure the app builds with the new dependency.
2.  **Settings Interaction**: Go to Settings and tap "Buy the Dev a Coffee".
3.  **Dialog Verification**: Verify the `DonationDialog` appears (Note: Prices might not show if not running on a real device with the app uploaded to Play Console).
4.  **Log Verification**: Check logs to see `BillingClient` connecting and attempting to fetch products.
