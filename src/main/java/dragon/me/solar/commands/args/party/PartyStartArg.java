package dragon.me.solar.commands.args.party;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.PartyManager;
import dragon.me.solar.party.PartyService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyStartArg {
    private final Solar plugin;
    private final PartyManager partyManager;
    private final PartyService partyService;
    private final KitManager kitManager;
    private final ArenaManager arenaManager;
    private final GridManager gridManager;
    private final MatchService matchService;
    private final MessageService messages;

    public PartyStartArg(
            Solar plugin,
            PartyManager partyManager,
            PartyService partyService,
            KitManager kitManager,
            ArenaManager arenaManager,
            GridManager gridManager,
            MatchService matchService,
            MessageService messages) {
        this.plugin = plugin;
        this.partyManager = partyManager;
        this.partyService = partyService;
        this.kitManager = kitManager;
        this.arenaManager = arenaManager;
        this.gridManager = gridManager;
        this.matchService = matchService;
        this.messages = messages;
    }

    @Command("party start <kit> <eventType>")
    public void start(
            CommandSourceStack stack,
            @Argument("kit") String kit,
            @Argument("eventType") String eventType) {
        if (!(stack.getSender() instanceof Player player)) {
            messages.sendConsoleError(stack);
            return;
        }

        InMemoryParty party = partyManager.getPartyByMember(player.getUniqueId());
        if (party == null) {
            messages.send(player, messages.language().notInParty());
            return;
        }
        if (!party.getOwner().equals(player.getUniqueId())) {
            messages.send(player, messages.language().notPartyLeader());
            return;
        }

        InMemoryKit inMemoryKit = kitManager.getKit(kit);
        if (inMemoryKit == null) {
            messages.send(
                    player, messages.language().kitNotFound(), Placeholder.parsed("kit", kit));
            return;
        }

        switch (eventType.toLowerCase()) {
            case "splitfight":
            case "split":
                startSplit(party, inMemoryKit);
                break;
            default:
                messages.send(player, messages.language().invalidEventType());
                break;
        }
    }

    private void startSplit(InMemoryParty party, InMemoryKit kit) {
        if (party.getMemberList().size() < 2) {
            return;
        }

        List<InMemoryTeam> teams = partyService.generateTeamsForSplit(party);
        int slot = gridManager.allocate();
        String arenaName = resolveArenaName();
        if (arenaName == null) {
            gridManager.free(slot);
            return;
        }

        BlockVector3 center = gridManager.getCenter(slot);
        FaweHook.pasteArena(arenaName, center)
                .thenAccept(
                        success -> {
                            if (!success) {
                                gridManager.free(slot);
                                plugin.getLogger().warning("Failed to paste arena " + arenaName);
                                return;
                            }
                            Bukkit.getScheduler()
                                    .runTask(plugin, () -> startMatch(teams, kit, arenaName, slot));
                        })
                .exceptionally(
                        throwable -> {
                            gridManager.free(slot);
                            plugin.getLogger()
                                    .log(
                                            Level.SEVERE,
                                            "Error while pasting arena " + arenaName,
                                            throwable);
                            return null;
                        });
    }

    private void startMatch(List<InMemoryTeam> teams, InMemoryKit kit, String arenaName, int slot) {
        World world = Bukkit.getWorld("arenas");
        if (world == null) {
            gridManager.free(slot);
            plugin.getLogger().severe("Arena world does not exist!");
            return;
        }

        InMemoryArena arena = arenaManager.getArena(arenaName);
        if (arena == null || arena.spawn1 == null || arena.spawn2 == null) {
            gridManager.free(slot);
            plugin.getLogger().warning("Arena spawn points missing for " + arenaName);
            return;
        }

        BlockVector3 center = gridManager.getCenter(slot);
        Location team1Spawn = arena.spawn1.toLocation(world, center);
        Location team2Spawn = arena.spawn2.toLocation(world, center);
        teleportTeam(teams.get(0), team1Spawn);
        teleportTeam(teams.get(1), team2Spawn);

        InMemoryMatch match = new InMemoryMatch(teams, kit.kitId, arenaName);
        match.setArenaName(arenaName);
        match.setGridSlot(slot);
        matchService.startMatch(match);
        plugin.getLogger().info("Starting the match between " + teams.size() + " teams. (Party)");
    }

    private void teleportTeam(InMemoryTeam team, Location location) {
        for (TeamPlayer teamPlayer : team.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player != null) {
                player.teleport(location);
            }
        }
    }

    private String resolveArenaName() {
        if (arenaManager.getArenas().isEmpty()) {
            return null;
        }
        return arenaManager.getArenas().values().stream()
                .skip(ThreadLocalRandom.current().nextInt(arenaManager.getArenas().size()))
                .findFirst()
                .map(arena -> arena.name)
                .orElse(null);
    }
}
