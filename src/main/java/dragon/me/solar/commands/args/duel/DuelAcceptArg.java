package dragon.me.solar.commands.args.duel;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.duel.record.DuelInviteRecord;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class DuelAcceptArg {
    private final Solar plugin;
    private final ArenaManager arenaManager;
    private final GridManager gridManager;
    private final DuelInviteManager duelInviteManager;
    private final MatchService matchService;
    private final MessageService messages;

    public DuelAcceptArg(
            Solar plugin,
            ArenaManager arenaManager,
            GridManager gridManager,
            DuelInviteManager duelInviteManager,
            MatchService matchService,
            MessageService messages) {
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.gridManager = gridManager;
        this.duelInviteManager = duelInviteManager;
        this.matchService = matchService;
        this.messages = messages;
    }

    @Command("duel accept <player>")
    public void accept(CommandSourceStack stack, @Argument("player") Player player) {
        if (!(stack.getSender() instanceof Player sender)) {
            messages.sendConsoleError(stack);
            return;
        }
        if (Solar.MAINTENANCE_MODE) {
            messages.send(sender, messages.language().maintenancePrevention());
            return;
        }

        DuelInviteRecord invite = duelInviteManager.getBySender(player.getUniqueId());
        if (invite == null || !invite.receiver().equals(sender.getUniqueId())) {
            return;
        }
        if (!duelInviteManager.removeBySender(player.getUniqueId())) {
            return;
        }

        messages.send(player, messages.language().matchBegin());
        messages.send(sender, messages.language().matchBegin());

        String arenaName = arenaManager.resolveArenaName(invite.map());
        if (arenaName == null) {
            plugin.getLogger().warning("No arena available for duel.");
            return;
        }

        int slot = gridManager.allocate();
        BlockVector3 center = gridManager.getCenter(slot);
        FaweHook.pasteArena(arenaName, center)
                .thenAccept(success -> {
                    if (!success) {
                        gridManager.free(slot);
                        plugin.getLogger().warning("Failed to paste arena " + arenaName);
                        return;
                    }
                    Bukkit.getScheduler()
                            .runTask(
                                    plugin,
                                    () -> startMatchAtArena(sender, player, invite, arenaName, slot, invite.rounds()));
                })
                .exceptionally(throwable -> {
                    gridManager.free(slot);
                    plugin.getLogger().log(Level.SEVERE, "Error while pasting arena " + arenaName, throwable);
                    return null;
                });
    }

    private void startMatchAtArena(
            Player sender, Player receiver, DuelInviteRecord invite, String arenaName, int slot, int rounds) {
        World world = Bukkit.getWorld("arenas");

        if (!sender.isOnline() || !receiver.isOnline()) {
            gridManager.free(slot);
            plugin.getLogger().warning("Players left before arena paste completed.");
            return;
        }

        InMemoryTeam team1 =
                new InMemoryTeam(new ArrayList<>(List.of(TeamPlayer.fromUuid(sender.getUniqueId()))), true);
        InMemoryTeam team2 =
                new InMemoryTeam(new ArrayList<>(List.of(TeamPlayer.fromUuid(receiver.getUniqueId()))), true);

        InMemoryMatch match = new InMemoryMatch(new ArrayList<>(List.of(team1, team2)), invite.kit());

        match.setArenaName(arenaName);
        match.setGridSlot(slot);
        match.setRounds(rounds);

        matchService.startMatch(match, false, null);
    }
}
