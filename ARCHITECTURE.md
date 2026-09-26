# Architecture

## Package Structure

- `arena`: arena definitions, persistence loading, and grid-slot allocation.
- `commands`: command entry points and argument parsing.
- `configs`: configuration loading and typed configuration records.
- `duel`, `party`, and `queue`: feature-specific state and coordination.
- `kit`: kit storage and kit application.
- `match`: match state, active-match lifecycle, and gameplay orchestration.
- `party`: party storage and party gameplay coordination.
- `listeners`: Bukkit event adapters that delegate to managers and services.
- `messages`: centralized MiniMessage rendering and player/console delivery.
- `application`: composition-root dependency graph for plugin managers and services.

Command handlers are organized under `commands/args/{arena,duel,kit,party}`. Arena operations are split into
`ArenaCreateArg`, `ArenaEdgeArg`, `ArenaSpawnArg`, `ArenaFinalizeArg`, and
`ArenaSaveSchematicArg`; `ArenaCommand` only supplies shared suggestions. Duel operations are split into
`DuelRequestArg`, `DuelAcceptArg`, and `DuelDeclineArg`; `DuelCommand` only supplies shared
suggestions. Party operations are split into `PartyCreateArg`, `PartyLeaveArg`, `PartyDisbandArg`,
`PartyInviteArg`, `PartyAcceptArg`, and `PartyStartArg`. Kit operations are split into
`KitCreateArg`, `KitSetItemsArg`, `KitSetEffectsArg`, `KitFinalizeArg`, `KitGiveArg`, and
`KitSetFlagsArg`; `KitCommand` only supplies shared suggestions. The former aggregate command
classes remain only as compatibility types and no longer own gameplay command handlers.
Simple party handlers share an injected `PartyCommandContext` containing party storage, invite
storage, configuration, and message rendering dependencies. `MessageService` injects the configured
prefix and owns common console/player delivery. This keeps command registration as the composition
root without making each handler depend on `Solar` globals.

Kit handlers follow the same pattern through `KitCommandContext`, which supplies kit storage,
configuration, kit application, and message services. This keeps command argument classes focused on
argument validation and workflow coordination rather than global dependency lookup.

## Responsibility Boundaries

Managers own in-memory lifecycle and lookup operations. `MatchManager` stores active matches and answers match-membership queries; it does not start, end, or otherwise mutate gameplay state. `KitManager` stores kit definitions and `PartyManager` stores party membership. They do not apply kits or construct gameplay teams. Arena and kit managers expose controlled lookup views instead of requiring callers to access internal maps.

Services coordinate gameplay and business workflows. `MatchService` handles match startup, countdown, player snapshots, result announcements, cleanup, and termination while depending on managers and platform/configuration services supplied through its constructor. `KitService` applies kit state to players, and `PartyService` creates teams for party events.

Match orchestration is split into focused collaborators: `MatchPlayerStateService` owns snapshots,
inventory restoration, teleportation, and health reset; `MatchAnnouncementService` owns result
messages and sounds; and `MatchCountdownService` owns scheduled match startup notifications.
`MatchService` coordinates these services and retains match lifecycle and arena cleanup decisions.

## Dependency Injection

`SolarContext` is the composition root for concrete managers and services. `Solar` creates it once,
then registers commands and listeners using the initialized graph. Existing `Solar` static fields are
currently retained as a compatibility bridge for legacy code; new code should receive dependencies
from the context or through constructors. `GridManager` receives `ConfigManager` directly, and
extracted command handlers receive the managers and services they coordinate.

## Benefits and Tradeoffs

This arrangement makes ownership explicit, keeps managers small, and makes service behavior easier to test with substitutes. It also reduces accidental coupling to mutable collections. The current plugin still integrates with Bukkit's static APIs and has several large command classes, so the refactor is incremental rather than a complete inversion of control. Further command extraction and platform adapters would improve testability but increase the number of types and wiring code.
