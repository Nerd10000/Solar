package dragon.me.solar.commands;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.duel.record.DuelInviteRecord;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.suggestion.Suggestions;

public class DuelCommand {

    private final MiniMessage miniMessage;
    private final KitManager kitManager;
    private final DuelInviteManager duelInviteManager;
    private final ConfigManager configManager;
    private final GridManager gridManager;
    private final MatchManager matchManager;
    private final MatchService matchService;
    private final ArenaManager arenaManager;

    public DuelCommand(MiniMessage miniMessage, KitManager kitManager, DuelInviteManager duelInviteManager, ConfigManager configManager, GridManager gridManager, MatchManager matchManager, MatchService matchService, ArenaManager arenaManager) {
        this.miniMessage = miniMessage;
        this.kitManager = kitManager;
        this.duelInviteManager = duelInviteManager;
        this.configManager = configManager;
        this.gridManager = gridManager;
        this.matchManager = matchManager;
        this.matchService = matchService;
        this.arenaManager = arenaManager;
    }

    @Command("duel <player> <kit>")
    public void duel(
            CommandSourceStack stack,
            @Argument("player") Player player,
            @Argument(value = "kit", suggestions = "kits") String kit,
            @Flag("rounds") @Default("1") Integer rounds,
            @Flag(value = "map", suggestions = "arenas") @Default("random") String map) {

        if (rounds == null) {
            rounds = 1;
        }

        if (map == null) {
            map = "random";
        }

        if (!(stack.getSender() instanceof Player sender)) {

            stack.getSender()
                    .sendMessage(
                            miniMessage.deserialize(
                                    configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            configManager.languageRecord().prefix())));
            return;
        }

        if (Solar.MAINTENANCE_MODE) {

            sender.sendMessage(
                    miniMessage.deserialize(
                            configManager.languageRecord().maintenancePrevention(),
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix())));
            return;
        }

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(kit);

        if (inMemoryKit == null) {
            sender.sendMessage(
                    miniMessage.deserialize(
                            configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", kit)));

            return;
        }

        DuelInviteRecord record =
                new DuelInviteRecord(
                        sender.getUniqueId(),
                        player.getUniqueId(),
                        kit,
                        map,
                        rounds,
                        System.currentTimeMillis());

        if (!Solar.duelInviteManager.add(record)) {
            sender.sendMessage(
                    miniMessage.deserialize(
                            configManager.languageRecord().tooManyDuelInvites(),
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix())));
            return;
        }

        sender.sendMessage(
                miniMessage.deserialize(
                        configManager.languageRecord().duelRequestSent(),
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("rounds", String.valueOf(rounds)),
                        Placeholder.parsed("map", map),
                        Placeholder.parsed("kit", kit),
                        Placeholder.parsed(
                                "prefix", configManager.languageRecord().prefix())));

        player.sendMessage(
                miniMessage.deserialize(
                        configManager.languageRecord().duelRequestReceived(),
                        Placeholder.parsed("prefix", configManager.languageRecord().prefix()),
                        Placeholder.parsed("player", sender.getName()),
                        Placeholder.parsed("rounds", String.valueOf(rounds)),
                        Placeholder.parsed("kit", kit),
                        Placeholder.parsed("map", map)));
    }

    @Command("duel accept <player>")
    public void duelAccept(CommandSourceStack stack, @Argument("player") Player player) {

        if (!(stack.getSender() instanceof Player sender)) {

            stack.getSender()
                    .sendMessage(
                            miniMessage.deserialize(
                                    configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            configManager.languageRecord().prefix())));

            return;
        }
        if (Solar.MAINTENANCE_MODE) {

            sender.sendMessage(
                    miniMessage.deserialize(
                            configManager.languageRecord().maintenancePrevention(),
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix())));
            return;
        }

        DuelInviteRecord inviteRecord = Solar.duelInviteManager.getBySender(player.getUniqueId());
        if (inviteRecord == null) {
            return;
        }

        if (!inviteRecord.receiver().equals(sender.getUniqueId())) {
            return;
        }

        DuelInviteRecord invite =
                new DuelInviteRecord(
                        inviteRecord.sender(),
                        inviteRecord.receiver(),
                        inviteRecord.kit(),
                        inviteRecord.map(),
                        inviteRecord.rounds(),
                        inviteRecord.timestamp());

        if (!Solar.duelInviteManager.removeBySender(player.getUniqueId())) {
            return;
        }

        player.sendMessage(
                miniMessage.deserialize(
                        configManager.languageRecord().matchBegin(),
                        Placeholder.parsed(
                                "prefix", configManager.languageRecord().prefix())));

        sender.sendMessage(
                miniMessage.deserialize(
                        configManager.languageRecord().matchBegin(),
                        Placeholder.parsed(
                                "prefix", configManager.languageRecord().prefix())));

        String arenaName = resolveArenaName(invite.map());
        if (arenaName == null) {
            Solar.instance.getLogger().warning("No arena available for duel.");
            return;
        }

        int slot = Solar.gridManager.allocate();
        BlockVector3 center = Solar.gridManager.getCenter(slot);

        FaweHook.pasteArena(arenaName, center)
                .thenAccept(
                        success -> {
                            if (!success) {
                                Solar.gridManager.free(slot);
                                Solar.instance
                                        .getLogger()
                                        .warning("Failed to paste arena " + arenaName);
                                return;
                            }

                            Bukkit.getScheduler()
                                    .runTask(
                                            Solar.instance,
                                            () ->
                                                    startMatchAtArena(
                                                            sender, player, invite, arenaName,
                                                            slot));
                        })
                .exceptionally(
                        throwable -> {
                            Solar.gridManager.free(slot);
                            Solar.instance
                                    .getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "Error while pasting arena " + arenaName,
                                            throwable);
                            return null;
                        });
    }

    private String resolveArenaName(String map) {
        if (map.equalsIgnoreCase("random")) {
            if (arenaManager.ARENAS.isEmpty()) {
                return null;
            }

            return arenaManager.ARENAS.values().stream()
                    .skip(ThreadLocalRandom.current().nextInt(arenaManager.ARENAS.size()))
                    .findFirst()
                    .orElseThrow()
                    .name;
        }

        if (!arenaManager.ARENAS.containsKey(map)) {
            return null;
        }

        return map;
    }

    private void startMatchAtArena(
            Player sender, Player receiver, DuelInviteRecord invite, String arenaName, int slot) {

        World world = Bukkit.getWorld("arenas");
        if (world == null) {
            gridManager.free(slot);
            Solar.instance.getLogger().severe("Arena world does not exist!");
            return;
        }

        if (!sender.isOnline() || !receiver.isOnline()) {
            gridManager.free(slot);
            Solar.instance.getLogger().warning("Players left before arena paste completed.");
            return;
        }

        InMemoryArena arena = arenaManager.getArena(arenaName);
        if (arena == null || arena.spawn1 == null || arena.spawn2 == null) {
            gridManager.free(slot);
            Solar.instance.getLogger().warning("Arena spawn points missing for " + arenaName);
            return;
        }

        BlockVector3 center = gridManager.getCenter(slot);
        Location senderLocation = arena.spawn1.toLocation(world, center);
        Location receiverLocation = arena.spawn2.toLocation(world, center);

        sender.teleport(senderLocation);
        receiver.teleport(receiverLocation);

        InMemoryTeam team1 =
                new InMemoryTeam(
                        new ArrayList<>(List.of(TeamPlayer.fromUuid(sender.getUniqueId()))), true);
        InMemoryTeam team2 =
                new InMemoryTeam(
                        new ArrayList<>(List.of(TeamPlayer.fromUuid(receiver.getUniqueId()))),
                        true);

        InMemoryMatch match = new InMemoryMatch(new ArrayList<>(List.of(team1, team2)),
                invite.kit(),
                invite.map());

        matchService.startMatch(match);

        Solar.instance
                .getLogger()
                .info(
                        "Started match between "
                                + sender.getName()
                                + " and "
                                + receiver.getName()
                                + " on arena "
                                + arenaName);
    }

    @Command("duel decline <player>")
    public void decline(CommandSourceStack stack, @Argument("player") Player target) {

        if (stack.getSender() instanceof Player p) {

            if (Solar.MAINTENANCE_MODE) {

                p.sendMessage(
                        miniMessage.deserialize(
                                configManager.languageRecord().maintenancePrevention(),
                                Placeholder.parsed(
                                        "prefix", configManager.languageRecord().prefix())));
            }

            p.sendMessage(
                    miniMessage.deserialize(
                            configManager.languageRecord().duelDeclined(),
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix()),
                            Placeholder.parsed("sender", target.getName())));

            duelInviteManager.removeBySender(target.getUniqueId());

            target.sendMessage(
                    miniMessage.deserialize(
                            configManager.languageRecord().duelDeclinedRequester(),
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix()),
                            Placeholder.parsed("target", p.getName())));

        } else {

            stack.getSender()
                    .sendMessage(
                            miniMessage.deserialize(
                                    configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            configManager.languageRecord().prefix())));
        }
    }

    @Suggestions("arenas")
    public List<String> arenaSuggestions() {

        return Solar.arenaManager.ARENAS.keySet().stream().toList();
    }

    @Suggestions("kits")
    public List<String> kitSuggestions() {

        return kitManager.KITS.keySet().stream().toList();
    }
}
