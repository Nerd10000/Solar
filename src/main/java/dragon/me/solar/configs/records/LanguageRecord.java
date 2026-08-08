package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/*
 *
 * This is record responsible for the language.yml file when accessing it in the plugin
 *
 */
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
        @Setting("kit-finalized") String kitFinalized,

        //Duel related messages
        @Setting("duel-request-expired") String duelRequestExpired,
        @Setting("duel-request-sent") String duelRequestSent,
        @Setting("duel-request-received") String duelRequestReceived) {

    public static final LanguageRecord DEFAULTS =
            new LanguageRecord(
                    "<i><b><gradient:#FCD05C:#A48022>Solar</gradient></b></i> <grey>→<reset> ",
                    "<prefix><red>This command can only be executed in-game!",
                    "<grey>Your ping is <color:#FCD05C><ping></color:#FCD05C>ms.</grey>",
                    "<color:#FCD05C><target></color:#FCD05C><grey>'s ping is"
                            + " <color:#FCD05C><ping></color:#FCD05C>ms.</grey>",
                    "<prefix><red>Hey! We have detected that the server does not have"
                        + " <bold><dependency></bold> installed! Please install it and restart the"
                        + " server!</red>",
                    "<prefix><red>The arena already exists!</red>",
                    "<prefix><gray>The arena named <color:#FCD05C><arena></color:#FCD05C> was"
                            + " created.</gray>",
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
                    "<prefix><gray>You have created a kit named <color:#FCD05C>'<kit></color>"
                            + " !</gray>",
                    "<prefix><red>The kit named '<kit>' already exists!</red>",
                    "<prefix><red>Kit named '<kit>' does not exist!</red>",
                    "<prefix><gray>The items of <color:#FCD05C>'<kit>'</color> have been"
                            + " set!</gray>",
                    "<prefix><gray>The potion effects of <color:#FCD05C>'<kit>'</color> have been"
                            + " set!</gray>",
                    "<prefix><gray>The kit <color:#FCD05C>'<kit>'</color> has been"
                            + " finalized!</gray>",
                    "<prefix><gray>The duel invite to <color:#FCD05C>'<player>'</color:#FCD05C>has <color:#FCD05C>expired</color:#FCD05C>!</gray>",
                    "<prefix><gray>The <color:#FCD05C>FT<rounds></color:#FCD05C> <color:#FCD05C><kit></color:#FCD05C> duel invite to <color:#FCD05C>'<player>'</color:#FCD05C> has been sent!</gray>",
                    "<prefix><gray><color:#FCD05C><player></color:#FCD05C> sent you a <color:#FCD05C>FT<rounds> <kit></color:#FCD05C> duel request! Click here to <click:run_command:'duel accept %player%'><green><b><u>ACCEPT</u></b></green></click> or <click:run_command:'duel decline %player%'><red><b><u>DECLINE</u></b></red></click>.</gray>");
}
