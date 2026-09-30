package dragon.me.solar.configs.records;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record LanguageRecord(
        @Setting("prefix") String prefix,
        @Setting("console-cant-run") String consoleCantRun,
        @Setting("ping-command") String pingCommand,
        @Setting("ping-other-command") String pingOtherCommand,
        @Setting("missing-dependency") String missingDependency,

        // Arena management
        @Setting("arena-already-exists") String arenaAlreadyExists,
        @Setting("arena-created") String arenaCreated,
        @Setting("arena-not-found") String arenaNotFound,
        @Setting("arena-edge-set") String arenaEdgeSet,
        @Setting("arena-spawn-set") String arenaSpawnSet,
        @Setting("arena-cant-be-finalized") String arenaCantBeFinalized,
        @Setting("arena-finalized") String arenaFinalized,
        @Setting("arena-schem-saved") String arenaSchemSaved,
        @Setting("arena-schem-error") String arenaSchemError,

        // Kit management
        @Setting("kit-created") String kitCreated,
        @Setting("kit-exists") String kitExists,
        @Setting("kit-not-found") String kitNotFound,
        @Setting("kit-items-set") String kitItemsSet,
        @Setting("kit-effects-set") String kitEffectsSet,
        @Setting("kit-flags-set") String kitFlagsSet,
        @Setting("kit-finalized") String kitFinalized,

        // Duel related messages
        @Setting("duel-request-expired") String duelRequestExpired,
        @Setting("duel-request-sent") String duelRequestSent,
        @Setting("duel-request-received") String duelRequestReceived,
        @Setting("too-many-duel-invites") String tooManyDuelInvites,
        @Setting("match-begin") String matchBegin,
        @Setting("duel-declined") String duelDeclined,
        @Setting("duel-declined-requester") String duelDeclinedRequester,
        @Setting("spectator-joined") String spectatorJoined,
        @Setting("spectator-left") String spectatorLeft,
        @Setting("match-not-found") String matchNotFound,
        @Setting("already-in-spectator") String alreadyInSprectator,
        @Setting("not-in-spectator") String notInSpectator,

        // Match related messages
        @Setting("player-died-to-player") String playerDiedToPlayer,
        @Setting("player-died-to-unknown-causes") String playerDiedToUnknownCauses,
        @Setting("match-won") String matchWon,
        @Setting("match-lost") String matchLost,
        @Setting("match-forfeit-win") String matchForfeitWin,
        @Setting("match-forfeit-loss") String matchForfeitLoss,
        @Setting("lobby-set") String lobbySet,

        // Maintenance related
        @Setting("maintenance-changed") String maintenanceChanged,
        @Setting("maintenance-prevention") String maintenancePrevention,

        // Party management
        @Setting("cant-create-party-as-member-of-a-party") String cantCreatePartyAsMemberOfAParty,
        @Setting("party-created") String partyCreated,
        @Setting("cant-do-party-as-member-only") String cantDoPartyAsMemberOnly,
        @Setting("party-disbanded") String partyDisbanded,
        @Setting("not-in-party") String notInParty,
        @Setting("player-joined-party") String playerJoinedParty,
        @Setting("party-invite-sent") String partyInviteSent,
        @Setting("party-invite-received") String partyInviteReceived,
        @Setting("no-invite-from-party") String notInviteFromParty,
        @Setting("player-left-party") String playerLeftParty,
        @Setting("no-such-party") String noSuchParty,
        @Setting("party-full") String partyFull,
        @Setting("not-party-leader") String notPartyLeader,
        @Setting("invalid-event-type") String invalidEventType,
        @Setting("party-owner-left") String partyOwnerLeft,
        @Setting("party-transfer") String partyTransfer,
        @Setting("party-ivnite-denied") String partyInviteDenied,
        @Setting("party-info") List<String> partyInfo,
        @Setting("party-kick-player") String partyKickPlayer,
        @Setting("party-broadcast") String partyBroadcast,
        @Setting("party-not-enough-players") String notEnoughPlayers,

        // Queue related
        @Setting("queue-exists") String queueExists,
        @Setting("queue-created") String queueCreated,
        @Setting("queue-not-exists") String queueNotExists,
        @Setting("queue-flags-set") String queueFlagsSet,

        // Help messages
        @Setting("party-help") List<String> partyHelp,
        @Setting("arenas-help") List<String> arenasHelp,
        @Setting("solar-help") List<String> solarHelp,
        @Setting("queue-help") List<String> queueHelp) {

    public static final LanguageRecord DEFAULTS = new LanguageRecord(
            "<i><b><gradient:#FCD05C:#A48022>Solar</gradient></b></i> <grey>→<reset> ",
            "<prefix><red>This command can only be executed in-game!",
            "<grey>Your ping is <color:#FCD05C><ping></color:#FCD05C>ms.</grey>",
            "<color:#FCD05C><target></color:#FCD05C><grey>'s ping is"
                    + " <color:#FCD05C><ping></color:#FCD05C>ms.</grey>",
            "<prefix><red>Hey! We have detected that the server does not have"
                    + " <bold><dependency></bold> installed! Please install it and restart the"
                    + " server!</red>",
            "<prefix><red>The arena already exists!</red>",
            "<prefix><gray>The arena named <color:#FCD05C><arena></color:#FCD05C> was" + " created.</gray>",
            "<prefix><red>Arena is not found!</red>",
            "<prefix><gray>You have set the <color:#FCD05C>#<edge></color:#FCD05C> edge of"
                    + " the arena named <color:#FCD05C><arena></color:#FCD05C>.</gray>",
            "<prefix><gray>You have set the <color:#FCD05C>#<spawn></color:#FCD05C> spawn"
                    + " of the arena named <color:#FCD05C><arena></color:#FCD05C>.</gray>",
            "<prefix><red>Hey! You can't finalize the arena setup as it is unfinished."
                    + " </red><gray>Make sure you have set the <color:#FCD05C>edges and spawn"
                    + " points</color:#FCD05C>!</gray>",
            "<prefix><gray>The arena has been <color:#FCD05C>finalized</color:#FCD05C>! Now"
                    + " please add kits to this arena which you can do with the"
                    + " <color:#FCD05C>/kit</color:#FCD05C> command</gray>!",
            "<prefix><gray>The arena has been saved in"
                    + " <color:#FCD05C>Solar/schems/<name>.schem</color:#FCD05C>!</gray>",
            "<prefix><red>The task for saving the schematic failed due to a critical error."
                    + " Check the console for more detail!",
            "<prefix><gray>You have created a kit named <color:#FCD05C>'<kit></color>" + " !</gray>",
            "<prefix><red>The kit named '<kit>' already exists!</red>",
            "<prefix><red>Kit named '<kit>' does not exist!</red>",
            "<prefix><gray>The items of <color:#FCD05C>'<kit>'</color> have been" + " set!</gray>",
            "<prefix><gray>The potion effects of <color:#FCD05C>'<kit>'</color> have been" + " set!</gray>",
            "<prefix><gray>The flags were set for <color:#FCD05C>'<kit>'</color>.</gray> ",
            "<prefix><gray>The kit <color:#FCD05C>'<kit>'</color> has been" + " finalized!</gray>",
            "<prefix><gray>The duel invite to <color:#FCD05C>'<player>'</color:#FCD05C>has"
                    + " <color:#FCD05C>expired</color:#FCD05C>!</gray>",
            "<prefix><gray>The <color:#FCD05C>FT<rounds></color:#FCD05C>"
                    + " <color:#FCD05C><kit></color:#FCD05C> duel invite to"
                    + " <color:#FCD05C>'<player>'</color:#FCD05C> has been sent!</gray>",
            "<prefix><gray><color:#FCD05C><player></color:#FCD05C> sent you a"
                    + " <color:#FCD05C>FT<rounds> <kit></color:#FCD05C> duel request! Click"
                    + " here to <click:run_command:'duel accept"
                    + " %player%'><green><b><u>ACCEPT</u></b></green></click> or"
                    + " <click:run_command:'duel decline"
                    + " %player%'><red><b><u>DECLINE</u></b></red></click>.</gray>",
            "<prefix><red>You can't send more than 1 duel invite at a time, please wait"
                    + " until it is declined or accepted!</red>",
            "<prefix><#FCD05C>The match will begin shortly!",
            "<prefix><gray>You have declined <color:#fcd05c>'<sender>'</color:#fcd05c>'s" + " duel request!</gray>",
            "<prefix><color:#fcd05c>'<target>'<gray> has declined you duel request!</gray>",
            "<prefix><gray><color:#fcd05c><spectator></color:#fcd05c> is now" + " spectating!</gray>",
            "<prefix><gray><color:#fcd05c><spectator></color:#fcd05c> is not spectating" + " anymore!",
            "<gray><color:#fcd05c>☠ <victim></color> died to the hands of"
                    + " <color:#fcd05c><killer></color:#fcd05c>!</gray>",
            "<prefix><red>You are already spectating! Please leave first.</red>",
            "<prefix><red>You need to be in spectator mode in order to leave!</red>",
            "<prefix><red>There is no match with that player!</red>",
            "<gray><color:#fcd05c>☠ <victim></color> died in" + " <color:#fcd05c>combat</color:#fcd05c>!</gray>",
            "<prefix><green>You won the duel!</green>",
            "<prefix><red>You lost the duel. Winner: <color:#FCD05C><winner></color></red>",
            "<prefix><green>You won by forfeit!</green>",
            "<prefix><red>You forfeited the duel. Winner:" + " <color:#FCD05C><winner></color></red>",
            "<prefix><gray>Lobby location has been set.</gray>",
            "<prefix><gray>Maintenance mode's was changed to" + " <color:#fcd05c>'<status>'</color:#fcd05c>!</gray>",
            "<prefix><red>Currently you can't start a new match, due to an ongoing"
                    + " maintenance. Contact the staff team for further information!</red>",
            "<prefix><red>You can't create a new party while in one!</red>",
            "<prefix><gray>The <color:#fcd05c>party</color:#fcd05c> has been"
                    + " <color:#fcd05c>created!</color:#fcd05c>.</gray>",
            "<prefix><red>You can't <action> the party as you are not the party" + " leader!</red>",
            "<prefix><gray>The party has been <color:#fcd05c>disbanded</color:#fcd05c> by"
                    + " <color:#fcd05c><owner></color:#fcd05c>!</gray>",
            "<prefix><red>You are not in a party!</red>",
            "<prefix><color:#fcd05c><player></color:#fcd05c> <gray>joined the" + " party!</gray>",
            "<prefix><color:#fcd05c><player></color:#fcd05c><gray> was invited to the" + " party!</gray>",
            "<prefix><gray>You have been invited to <color:#fcd05c><player>'s"
                    + " party</color:#fcd05c></gray>! <click:run_command:'party accept"
                    + " <player>'><green><b><u>ACCEPT</u></b></green></click>"
                    + " <click:run_command:'party decline"
                    + " <player>'><red><b><u>DECLINE</u></b></red></click>",
            "<prefix><red>You have no party invites from that party!",
            "<prefix><gray><color:#fcd05c><player></color:#fcd05c> left the party!</gray>",
            "<prefix><red>Party not found!</red>",
            "<prefix><red>The party is full!</red>",
            "<prefix><red>You are not the leader of the party!</red>",
            "<prefix><red>Invalid event type! Use 'splitfight' or 'ffa'.</red>",
            "<prefix><gray>The party has been disbanded due to <color:#fcd05c><player></color:#fcd05c> (owner) leaving the server!",
            "<prefix><gray>The ownership of the party has been transferred to <color:#fcd05c><player></color:#fcd05c>.</gray>",
            "<prefix><gray><color:#fcd05c><player></color:#fcd05c>'s party's invite was declined!</gray>",
            List.of(
                    "",
                    "<b><u><gradient:#FCD05C:#A48022>Information about the party</u></b>",
                    "<color:#fcd05c>Owner: </color:#fcd05c><gray><owner></gray>",
                    "<color:#fcd05c>Members: </color:#fcd05c><gray><members></gray>"),
            "<prefix><gray><color:#fcd05c><player></color:#fcd05c> was kicked by the party leader!</gray>",
            "<prefix><gray>You have been invited to <color:#fcd05c><player>'s party</color:#fcd05c>! Click here to <click:run_command:'party accept <player>'><green><b><u>ACCEPT</u></b></green></click> </gray>",
            "<prefix><red>You can't start the split fight due to lack of players! You must have at least 2 players to start a party event!",
            "<prefix><red>A name with that queue already exists",
            "<prefix><gray>The <color:#fcd05c>'<queue>'</color:#fcd05c> queue was successfully created!</gray>",
            "<prefix><red>The specified '<queue>' queue does not exists!</red>",
            "<prefix><gray>The <color:#fcd05c>'<queue>'</color:#fcd05c> queue's flags have been set!</gray>",
            List.of(
                    "<b><gradient:#FCD05C:#A48022><u>Help for Parties:</u></b>",
                    "<gradient:#FCD05C:#A48022>/party create <gray>- Creates the party.",
                    "<gradient:#FCD05C:#A48022>/party invite <player> <gray>- Invites the"
                            + " specified player to the party!",
                    "<gradient:#FCD05C:#A48022>/party accept <player> <gray>- Accepts the" + " party invite.",
                    "<gradient:#FCD05C:#A48022>/party disband <gray>- Disbands the party.",
                    "<gradient:#FCD05C:#A48022>/party leave <gray>- Leaves the party.",
                    "<gradient:#FCD05C:#A48022>/party start <kit> <type> <gray>- Starts the"
                            + " party with the specified kit and type (Types: FFA, Split)"),
            List.of(
                    "<b><gradient:#FCD05C:#A48022><u>Help for Arenas:</u></b>",
                    "<gradient:#FCD05C:#A48022>/arenas create <name> <gray>- Creates an" + " arena named <arena>.",
                    "<gradient:#FCD05C:#A48022>/arenas setEdge <arena> <1 | 2> <gray>- Sets"
                            + " te #1 or the #2 corner of an arena.",
                    "<gradient:#FCD05C:#A48022>/arenas setSpawn <arena> <1 | 2> <gray>-"
                            + " Sets the #1 or the #2 team spawn for the arena.",
                    "<gradient:#FCD05C:#A48022>/arenas finalize <arena> <gray>-"
                            + " Saves/Finalizes the arena to the disk.",
                    "<gradient:#FCD05C:#A48022>/arenas saveSchematic <arena> <gray>-"
                            + " Creates a schematic of the arena which is used for cloning it"
                            + " when a match starts! "),
            List.of(
                    "<b><gradient:#FCD05C:#A48022><u>Help for Solar:</u></b>",
                    "<gradient:#FCD05C:#A48022>/solar setlobby <gray>- Sets the lobby location (where the players will be teleported after a match)",
                    "<gradient:#FCD05C:#A48022>/solar reload <gray>- Reloads the configuration files.",
                    "<gradient:#FCD05C:#A48022>/solar maintenance <gray>- Turn on or off maintenance mode. (Players will not be able to start a new match!)"),
            List.of(
                    "<b><gradient:#FCD05C:#A48022><u>Help for Queue:</u></b>",
                    "<gradient:#FCD05C:#A48022>/queue create <name> <kit> <gray>- Creates a queue.",
                    "<gradient:#FCD05C:#A48022>/queue set-flags <name> <flags...> <gray>- Sets the flags for the queue which is specified.",
                    "<gradient:#FCD05C:#A48022>/queue finalize <name> <gray>- Saves/Finalizes the specified queue."));
}
