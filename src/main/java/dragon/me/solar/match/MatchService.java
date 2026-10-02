package dragon.me.solar.match;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.kit.KitService;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.ratings.RatingService;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.match.utils.MatchStageEnum;
import dragon.me.solar.queue.InMemoryQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MatchService {

    private final MatchManager matchManager;
    private final GridManager gridManager;
    private final ConfigManager configManager;
    private final KitManager kitManager;
    private final KitService kitService;
    private final MatchPlayerStateService playerStateService;
    private final MatchAnnouncementService announcementService;
    private final MatchCountdownService countdownService;
    private final ArenaManager arenaManager;

    public MatchService(
            MatchManager matchManager,
            GridManager gridManager,
            ConfigManager configManager,
            KitManager kitManager,
            KitService kitService,
            MatchPlayerStateService playerStateService,
            MatchAnnouncementService announcementService,
            MatchCountdownService countdownService,
            ArenaManager arenaManager) {
        this.matchManager = matchManager;
        this.gridManager = gridManager;
        this.configManager = configManager;
        this.kitManager = kitManager;
        this.kitService = kitService;
        this.playerStateService = playerStateService;
        this.announcementService = announcementService;
        this.countdownService = countdownService;
        this.arenaManager = arenaManager;
    }

    public void startMatch(InMemoryMatch match, boolean isFFA, @Nullable InMemoryQueue queue) {

        match.setStage(MatchStageEnum.STARTING);

        BlockVector3 center = gridManager.getCenter(match.getGridSlot());
        FaweHook.pasteArena(match.getArenaName(), center)
                .thenAccept(success -> {
                    Bukkit.getScheduler().runTask(Solar.instance, () -> {
                        if (!success) {
                            gridManager.free(match.getGridSlot());
                            Solar.instance.getLogger().warning("Failed to paste arena");
                            return;
                        }

                        matchManager.add(match);
                        playerStateService.saveSnapshots(match);
                        validateAndApplyKit(match);
                        countdownService.start(match);

                        if (!prepareArena(match, isFFA)) {
                            return;
                        }
                    });
                })
                .exceptionally(throwable -> {
                    Bukkit.getScheduler().runTask(Solar.instance, () -> {
                        gridManager.free(match.getGridSlot());
                        Solar.instance
                                .getLogger()
                                .log(Level.SEVERE, "Error while pasting arena " + match.getArenaName(), throwable);
                    });
                    return null;
                });
    }

    private boolean prepareArena(InMemoryMatch match, boolean isFFA) {

        World world = Bukkit.getWorld("arenas");

        if (world == null) {
            gridManager.free(match.getGridSlot());
            Solar.instance.getLogger().severe("Arena world does not exist!");
            return false;
        }

        InMemoryArena arena = arenaManager.getArena(match.getArenaName());

        BlockVector3 center = gridManager.getCenter(match.getGridSlot());

        if (arena == null || arena.spawn1 == null || arena.spawn2 == null) {
            gridManager.free(match.getGridSlot());
            Solar.instance.getLogger().warning("Arena spawn points missing for " + match.getArenaName());
            return false;
        }

        Location team1Spawn = arena.spawn1.toLocation(world, center);
        Location team2Spawn = arena.spawn2.toLocation(world, center);

        if (!isFFA) {

            teleportTeam(match.getTeamList().get(0), team1Spawn);
            teleportTeam(match.getTeamList().get(1), team2Spawn);

        } else {
            int playerCount = match.getTeamList().size();
            double rotation = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
            for (int index = 0; index < playerCount; index++) {
                InMemoryTeam team = match.getTeamList().get(index);

                double angle = rotation + (2 * Math.PI * index) / playerCount;

                int x = (int) Math.round(
                        center.x() + Math.cos(angle) * 5 // TODO make it customizable
                        );

                int z = (int) Math.round(
                        center.z() + Math.sin(angle) * 5 // TODO make it customizable
                        );

                Location onGroundCenter = world.getHighestBlockAt(x, z).getLocation();

                Player player = Bukkit.getPlayer(team.getMembers().get(0).uuid());

                if (player != null) {
                    player.teleport(onGroundCenter.add(0.5, 1, 0.5));
                }
            }
        }

        Solar.instance
                .getLogger()
                .info("Starting the match between " + match.getTeamList().size() + " teams. (Party)");
        return true;
    }

    private void teleportTeam(InMemoryTeam team, Location location) {
        for (TeamPlayer teamPlayer : team.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player != null) {
                player.teleport(location);
            }
        }
    }

    public void endMatch(InMemoryMatch match, InMemoryTeam winner, MatchEndReason reason) {

        if (match.getStage() == MatchStageEnum.ENDED) {
            return;
        }

        match.setStage(MatchStageEnum.ENDED);
        if (winner != null) {
            match.setWinner(winner);
        }

        playerStateService.clearInventories(match);
        String winnerName = announcementService.resolveWinnerName(winner);
        announcementService.announceResults(match, winner, winnerName, reason);
        playerStateService.restoreAndTeleport(match);
        playerStateService.resetMaxHealth(match);

        for (InMemoryTeam team : match.getTeamList()) {

            boolean isWinner = team.equals(winner);

            for (TeamPlayer tp : team.getMembers()) {
                Solar.cache
                        .getPlayer(tp.uuid(), match.getKit())
                        .thenCompose(stat -> {
                            if (isWinner) {
                                stat.setWins(stat.getWins() + 1);

                            } else {
                                stat.setLosses(stat.getLosses() + 1);
                            }

                            return Solar.databaseManager.updateStats(stat);
                        })
                        .exceptionally(error -> {
                            Solar.instance.getLogger().severe("Failed to update stats for " + tp.uuid());
                            error.printStackTrace();
                            return null;
                        });
            }

            if (match.getTeamList().size() == 2 && match.getMatchSource() != null && winner != null) {
                RatingService.updateRating(
                        winner,
                        match.getTeamList().stream()
                                .filter(team_ -> team_.equals(winner))
                                .findFirst()
                                .get(),
                        match.getMatchSource());
            }
        }

        Bukkit.getScheduler()
                .runTaskLater(
                        Solar.instance,
                        () -> {
                            cleanupArena(match);
                            match.getSavedInventories().clear();
                            matchManager.remove(match.getUuid());
                        },
                        20 * 5L);
    }

    public void terminate(InMemoryMatch match) {
        endMatch(match, null, MatchEndReason.TERMINATED);
    }

    private void validateAndApplyKit(InMemoryMatch match) {
        for (var teamPlayer : match.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player == null) {
                return;
            }
            if (match.getKit() == null) {
                throw new IllegalStateException("Match has no kit!");
            }

            InMemoryKit kit = kitManager.getKit(match.getKit());
            if (kit == null) {
                throw new IllegalStateException("No such a kit as '" + match.getKit() + "'! ");
            }

            kitService.applyKit(player, kit);
            if (kit.flags.maxHealth() != 20 && kit.flags.maxHealth() != -1) {
                player.setMaxHealth(kit.flags.maxHealth());
            }
            player.heal(Integer.MAX_VALUE);
            player.setFoodLevel(20);
        }
    }

    private void cleanupArena(InMemoryMatch match) {
        int gridSlot = match.getGridSlot();
        String arenaName = match.getArenaName();
        if (gridSlot < 0) {
            return;
        }

        gridManager.free(gridSlot);
        if (arenaName != null) {
            BlockVector3 center = gridManager.getCenter(gridSlot);
            FaweHook.clearArenaSync(arenaName, center);
        }
    }
}
