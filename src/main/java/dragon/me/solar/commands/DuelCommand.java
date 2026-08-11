package dragon.me.solar.commands;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.duel.record.DuelInviteRecord;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;

public class DuelCommand {

    @Command("duel <player> <kit>")
    public void duel(
            CommandSourceStack stack,
            @Argument("player") Player player,
            @Argument("kit") String kit,
            @Flag("rounds") @Default("1") int rounds,
            @Flag("map") @Default("random") String map) {

        if (!(stack.getSender() instanceof Player sender)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(kit);

        if (inMemoryKit == null) {
            sender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
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
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().tooManyDuelInvites(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        sender.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().duelRequestSent(),
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("rounds", String.valueOf(rounds)),
                        Placeholder.parsed("map", map),
                        Placeholder.parsed("kit", kit),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));

        player.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().duelRequestReceived(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
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
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));

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
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().matchBegin(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));

        sender.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().matchBegin(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));

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
            if (Solar.arenaManager.ARENAS.isEmpty()) {
                return null;
            }

            return Solar.arenaManager.ARENAS.values().stream()
                    .skip(ThreadLocalRandom.current().nextInt(Solar.arenaManager.ARENAS.size()))
                    .findFirst()
                    .orElseThrow()
                    .name;
        }

        if (!Solar.arenaManager.ARENAS.containsKey(map)) {
            return null;
        }

        return map;
    }

    private void startMatchAtArena(
            Player sender, Player receiver, DuelInviteRecord invite, String arenaName, int slot) {

        World world = Bukkit.getWorld("arenas");
        if (world == null) {
            Solar.gridManager.free(slot);
            Solar.instance.getLogger().severe("Arena world does not exist!");
            return;
        }

        if (!sender.isOnline() || !receiver.isOnline()) {
            Solar.gridManager.free(slot);
            Solar.instance.getLogger().warning("Players left before arena paste completed.");
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(arenaName);
        if (arena == null || arena.spawn1 == null || arena.spawn2 == null) {
            Solar.gridManager.free(slot);
            Solar.instance.getLogger().warning("Arena spawn points missing for " + arenaName);
            return;
        }

        BlockVector3 center = Solar.gridManager.getCenter(slot);
        Location senderLocation = arena.spawn1.toLocation(world, center);
        Location receiverLocation = arena.spawn2.toLocation(world, center);

        sender.teleport(senderLocation);
        receiver.teleport(receiverLocation);

        Solar.matchManager.startMatch(
                invite.sender(), invite.receiver(), invite.kit(), invite.map(), arenaName, slot);

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
}
