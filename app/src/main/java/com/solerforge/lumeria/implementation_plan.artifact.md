# Implementation Plan: Add 8 Mythic Races, Classes, and Skills

This plan covers adding 4 Mythic hero races and 4 Mythic monster races, each with custom classes and specialized skills.

## User Review Required

> [!IMPORTANT]
> The Mythic rarity is a new tier above God Tier.
> Mythic races will have extremely high stat bonuses and specialized skills.
> Faction-exclusive restrictions will be maintained.

## Proposed Changes

### [Models]

#### [MODIFY] [Race.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/solerforge/lumeria/models/Race.kt)
- Add `Mythic` to `RaceRarity` enum with a unique color (e.g., Deep Crimson or Rainbow).

### [Databases]

#### [MODIFY] [RaceDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/solerforge/lumeria/database/RaceDatabase.kt)
- Add 4 Hero Mythic Races:
    - **Astral Celestial**: Cosmic energy, high INT/WIS/AGI.
    - **Solar Archon**: Sun's fury, high STR/VIT/DEF.
    - **Lunar Sovereign**: Night's rule, high AGI/LUCK/WIS.
    - **Genesis Primordial**: Original creators, balanced extreme stats.
- Add 4 Monster Mythic Races:
    - **Void Abomination**: Absolute cosmic horror, extreme STR/INT/AGI.
    - **Chaos Overlord**: Entropic ruler, high INT/LUCK/VIT.
    - **Abyssal Devourer**: World eater, extreme STR/VIT.
    - **Eternal Singularity**: Gravitational collapse, extreme DEF/VIT/WIS.
- Adjust `rollChance` for existing races slightly if needed (though Mythic will likely be 0.000001% or similar).

#### [MODIFY] [ClassDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/solerforge/lumeria/database/ClassDatabase.kt)
- Add custom classes for each Mythic race:
    - Hero: `Cosmic Envoy`, `Sun Lord`, `Moon Goddess`, `World Weaver`.
    - Monster: `Eldritch Terror`, `Entropic God`, `Planet Eater`, `Singularity Core`.

#### [MODIFY] [SkillDatabase.kt](file:///C:/Users/buggm/AndroidStudioProjects/Lumeria/app/src/main/java/com/solerforge/lumeria/database/SkillDatabase.kt)
- Add 3 specialized skills for each of the 8 new races/classes (total 24 new skills).
- These skills will be high-tier with unique effects (IgnoreArmor, MultiHit, Lifesteal, etc.).

## Verification Plan

### Automated Tests
- Check if all new races are correctly added to the database.
- Verify class requirements for new races.
- Ensure skills are correctly mapped to new classes.

### Manual Verification
- Deploy the app and check the character creation / rebirth screens (if Mythic is unlockable/rollable).
- Verify stats in the Bestiary/Codex if applicable.
- Test new skills in Battle (if possible to force a mythic character).
