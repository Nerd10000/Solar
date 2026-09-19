package dragon.me.solar.match;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.kit.KitService;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MatchService {

    private final MatchManager matchManager;
    private final GridManager gridManager;
    private final ConfigManager configManager;
    private final KitManager kitManager;
    private final KitService kitService;
    private final MatchPlayerStateService playerStateService;
    private final MatchAnnouncementService announcementService;
    private final MatchCountdownService countdownService;

    public MatchService(
            MatchManager matchManager,
            GridManager gridManager,
            ConfigManager configManager,
            KitManager kitManager,
            KitService kitService,
            MatchPlayerStateService playerStateService,
            MatchAnnouncementService announcementService,
            MatchCountdownService countdownService) {
        this.matchManager = matchManager;
        this.gridManager = gridManager;
        this.configManager = configManager;
        this.kitManager = kitManager;
        this.kitService = kitService;
        this.playerStateService = playerStateService;
        this.announcementService = announcementService;
        this.countdownService = countdownService;
    }

    public void startMatch(InMemoryMatch match) {
        match.setStage(MatchStageEnum.STARTING);
        matchManager.add(match);
        playerStateService.saveSnapshots(match);
        validateAndApplyKit(match);
        countdownService.start(match);
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
