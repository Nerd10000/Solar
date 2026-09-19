# Refactor Changelog

## Changed Files

- `src/main/java/dragon/me/solar/Solar.java`
- `src/main/java/dragon/me/solar/arena/ArenaManager.java`
- `src/main/java/dragon/me/solar/commands/ArenaCommand.java`
- `src/main/java/dragon/me/solar/commands/DuelCommand.java`
- `src/main/java/dragon/me/solar/commands/args/duel/DuelAcceptArg.java`
- `src/main/java/dragon/me/solar/commands/args/duel/DuelRequestArg.java`
- `src/main/java/dragon/me/solar/commands/args/duel/DuelDeclineArg.java`
- `src/main/java/dragon/me/solar/commands/args/arena/ArenaCreateArg.java`
- `src/main/java/dragon/me/solar/commands/args/arena/ArenaEdgeArg.java`
- `src/main/java/dragon/me/solar/commands/args/arena/ArenaSpawnArg.java`
- `src/main/java/dragon/me/solar/commands/args/arena/ArenaFinalizeArg.java`
- `src/main/java/dragon/me/solar/commands/args/arena/ArenaSaveSchematicArg.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitCreateArg.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitSetItemsArg.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitSetEffectsArg.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitFinalizeArg.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitGiveArg.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitSetFlagsArg.java`
- `src/main/java/dragon/me/solar/commands/KitCommand.java`
- `src/main/java/dragon/me/solar/commands/PartyCommand.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyStartArg.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyCreateArg.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyLeaveArg.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyDisbandArg.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyInviteArg.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyAcceptArg.java`
- `src/main/java/dragon/me/solar/commands/args/party/PartyCommandContext.java`
- `src/main/java/dragon/me/solar/arena/GridManager.java`
- `src/main/java/dragon/me/solar/kit/KitManager.java`
- `src/main/java/dragon/me/solar/kit/KitService.java`
- `src/main/java/dragon/me/solar/party/PartyManager.java`
- `src/main/java/dragon/me/solar/party/PartyService.java`
- `src/main/java/dragon/me/solar/messages/MessageService.java`
- `src/main/java/dragon/me/solar/commands/args/kit/KitCommandContext.java`
- `src/main/java/dragon/me/solar/application/SolarContext.java`
- `src/main/java/dragon/me/solar/match/MatchManager.java`
- `src/main/java/dragon/me/solar/match/MatchService.java`
- `src/main/java/dragon/me/solar/match/MatchPlayerStateService.java`
- `src/main/java/dragon/me/solar/match/MatchAnnouncementService.java`
- `src/main/java/dragon/me/solar/match/MatchCountdownService.java`
- `.gitignore`
- `src/main/java/dragon/me/solar/listeners/BlockBreakAndPlaceListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerDeathEventListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerDropItemListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerItemUseListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerJoinListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerMovementListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerPickupItemListener.java`
- `src/main/java/dragon/me/solar/listeners/PlayerQuitEventListener.java`
- `ARCHITECTURE.md`
- `REFACTOR_CHANGELOG.md`

## Added Files

- `ARCHITECTURE.md`
- `REFACTOR_CHANGELOG.md`

## Removed Files

- None.

## Refactoring

- Separated active-match lifecycle storage in `MatchManager` from match gameplay orchestration in `MatchService`.
- Replaced direct access to manager internals with read-only lookup APIs.
- Repaired dependency injection and startup ordering for commands and services.
- Extracted duel acceptance and party match-start workflows into focused command argument classes.
- Consolidated command argument classes under `src/main/java/dragon/me/solar/commands/args/{arena,duel,kit,party}`.
- Split all party command operations into focused argument classes and removed command registration from the aggregate `PartyCommand`.
- Split duel request and decline operations into focused argument classes, leaving `DuelCommand` responsible only for suggestions.
- Split all arena management operations into focused argument classes, leaving `ArenaCommand` responsible only for suggestions.
- Split all kit management operations into focused argument classes, leaving `KitCommand` responsible only for suggestions.
- Added `PartyCommandContext` and migrated the simple party argument handlers to constructor-injected dependencies.
- Added `MessageService` to centralize MiniMessage rendering, prefix injection, and console/player delivery.
- Migrated the simple party and duel argument handlers to use the centralized message façade.
- Injected `ConfigManager` into `GridManager` and removed its static configuration dependency.
- Moved player kit application from `KitManager` to `KitService`.
- Moved party team generation from `PartyManager` to `PartyService`.
- Split `MatchService` player state, result announcements, and countdown scheduling into focused services.
- Added `SolarContext` as the composition root for manager and service construction, with static Solar fields retained temporarily as a compatibility bridge.
- Added `KitCommandContext` and migrated all kit argument handlers away from direct Solar static access.
- Removed stale imports and migrated callers to the current package and service APIs.
- Applied the configured Google Java formatter to all modified Java sources.

## Behavioral Changes

- No intentional gameplay behavior changes.
- Match termination now iterates over a stable snapshot of active matches.

## TODO

- Continue replacing remaining static plugin access with injected services incrementally.
- Add unit tests for manager lifecycle and match end flows.
- Extract the remaining command and listener behavior from `Solar` static state.
- Migrate remaining commands, listeners, and infrastructure classes away from static Solar access.
