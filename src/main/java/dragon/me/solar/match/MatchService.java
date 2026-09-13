package dragon.me.solar.match;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.match.player.PlayerSnapshot;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.match.utils.MatchStageEnum;
import dragon.me.solar.utils.SoundUtils;
import java.util.Iterator;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchService {

    private final MatchManager matchManager;

    public MatchService(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    public void startMatch(InMemoryMatch match) {

        // 0. Label the match as STARTING
        match.setStage(MatchStageEnum.STARTING);

        // 1. Register the match.

        matchManager.add(match);

        // 2. Lets save the player's current status (with snapshots)

        savePlayerSnapshots(match);

        // 3. Validate kit and give the kit to the players.

        validateAndApplyKit(match);

        // 4. Start the countdown

        countdown(match);
    }

    public void endMatch(InMemoryMatch match, InMemoryTeam winner, MatchEndReason reason) {

        if (match.getStage() == MatchStageEnum.ENDED) return;

        // 1. Set the match's status to ENDED
        match.setStage(MatchStageEnum.ENDED);

        // 2. Set the match's winner to the winner team

        if (winner != null) {
            match.setWinner(winner);
        }

        // 3. Clearing the match's participant's inventories

        for (TeamPlayer tp : match.getMembers()) {

            Player p = Bukkit.getPlayer(tp.uuid());
            if (p == null) continue;

            p.getInventory().clear();
        }

        // 4. Announce the results

        String winnerName = resolveWinnerName(winner);
        announceResults(match, winner, winnerName, reason);

        // 5. Restore the player's inventories and tp to lobby

        restoreAndTp(match);

        // 6. Free up the arena

        int gridSlot = match.getGridSlot();
        String arenaName = match.getArenaName();

        // 6.1  Clear the saved inventories
        match.getSavedInventories().clear();

        // 6.2 Restore the max health of players that was modified during the match.
        for (TeamPlayer tp : match.getMembers()) {

            Player p = Bukkit.getPlayer(tp.uuid());

            if (p == null) continue;

            p.setMaxHealth(20);
        }

        // 6.3 Free up the arena slot

        if (gridSlot >= 0) {
            Solar.gridManager.free(gridSlot);
        }

        if (arenaName != null && gridSlot >= 0) {

            BlockVector3 center = Solar.gridManager.getCenter(gridSlot);
            FaweHook.clearArenaSync(
                    arenaName, center); // For some weird reason async does not work here
        }

        Solar.matchManager.remove(match.getUuid());
    }

    private void restoreAndTp(InMemoryMatch match) {

        Location lobby = Solar.configManager.getLobbyLocation();

        for (TeamPlayer tp : match.getMembers()) {

            Player p = Bukkit.getPlayer(tp.uuid());

            if (p == null) continue;

            if (p.isDead())
                Bukkit.getScheduler().runTaskLater(Solar.instance, () -> p.spigot().respawn(), 1L);

            PlayerSnapshot snapshot = match.getSavedInventories().get(tp.uuid());

            if (snapshot != null) {
                snapshot.restore(p);
            }

            if (lobby != null) {
                long delay = 5 * 20L; // TODO: Make it configurable
                Bukkit.getScheduler()
                        .runTaskLater(
                                Solar.instance,
                                () -> {
                                    if (p.isOnline()) {
                                        p.teleport(lobby);
                                        p.setInvulnerable(false);
                                        p.setAllowFlight(false);
                                        p.setFireTicks(0);
                                        p.heal(Integer.MAX_VALUE);
                                        p.setFoodLevel(20);
                                    }
                                },
                                delay);
            }
        }
    }

    private void announceResults(
            InMemoryMatch match, InMemoryTeam winner, String winnerName, MatchEndReason reason) {

        for (TeamPlayer tp : match.getMembers()) {

            boolean isWinner = winner != null && match.getTeamByMember(tp.uuid()).equals(winner);

            Player p = Bukkit.getPlayer(tp.uuid());

            if (p == null) continue;

            if (reason == MatchEndReason.FORFEIT) {

                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                isWinner
                                        ? Solar.configManager.languageRecord().matchForfeitWin()
                                        : Solar.configManager.languageRecord().matchForfeitLoss(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix()),
                                Placeholder.parsed("winner", winnerName)));
                continue;
            }

            if (isWinner)
                SoundUtils.playConfiguredSound(
                        p,
                        Solar.configManager.settingsRecord().soundRecords().wonSound(),
                        Sound.UI_TOAST_CHALLENGE_COMPLETE);
            else
                SoundUtils.playConfiguredSound(
                        p,
                        Solar.configManager.settingsRecord().soundRecords().lostSound(),
                        Sound.BLOCK_BEACON_DEACTIVATE);

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            isWinner
                                    ? Solar.configManager.languageRecord().matchWon()
                                    : Solar.configManager.languageRecord().matchLost(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("winner", winnerName)));
        }
    }

    private String resolveWinnerName(InMemoryTeam winner) {

        if (winner == null) return "Unknown";

        Iterator<TeamPlayer> iterator = winner.getMembers().iterator();

        if (!iterator.hasNext()) return "Unknown";

        Player player = Bukkit.getPlayer(iterator.next().uuid());
        return player != null ? player.getName() : "Unknown";
    }

    private void savePlayerSnapshots(InMemoryMatch match) {

        for (TeamPlayer tp : match.getMembers()) {

            Player p = Bukkit.getPlayer(tp.uuid());

            if (p == null) continue;

            match.saveInventory(p);
        }
    }

    private void validateAndApplyKit(InMemoryMatch match) {

        for (TeamPlayer tp : match.getMembers()) {

            Player p = Bukkit.getPlayer(tp.uuid());

            if (p == null) return;

            if (match.getKit() == null) throw new IllegalStateException("Match has no kit!");

            InMemoryKit kit = Solar.kitManager.getKit(match.getKit());

            if (kit == null)
                throw new IllegalStateException("No such a kit as '" + kit.kitId + "'! ");

            Solar.kitManager.applyKit(p, kit);

            if (kit.flags.maxHealth() != 20 && kit.flags.maxHealth() != -1)
                p.setMaxHealth(kit.flags.maxHealth());

            p.heal(Integer.MAX_VALUE);
            p.setFoodLevel(20);
        }
    }

    private void countdown(InMemoryMatch match) {

        final int seconds = 3; // TODO Make it configurable

        for (int i = 0; i < seconds; i++) {

            final int secondsLeft = seconds - i;

            Bukkit.getScheduler()
                    .runTaskLater(
                            Solar.instance,
                            () -> {
                                String countdownStart =
                                        Solar.configManager
                                                .settingsRecord()
                                                .soundRecords()
                                                .countdownSound();

                                for (TeamPlayer tp : match.getMembers()) {

                                    Player p = Bukkit.getPlayer(tp.uuid());

                                    if (p == null) continue;

                                    SoundUtils.playConfiguredSound(
                                            p, countdownStart, Sound.BLOCK_NOTE_BLOCK_PLING);
                                    p.sendMessage(
                                            Solar.miniMessage.deserialize(
                                                    "<prefix><gray>Match"
                                                            + " starts"
                                                            + " in <color:#FCD05C>" // TODO Make
                                                            // this
                                                            // configurable!
                                                            + secondsLeft
                                                            + "</color>...</gray>",
                                                    Placeholder.parsed(
                                                            "prefix",
                                                            Solar.configManager
                                                                    .languageRecord()
                                                                    .prefix())));
                                }
                            },
                            i * 20L);
        }

        Bukkit.getScheduler()
                .runTaskLater(
                        Solar.instance,
                        () -> {
                            match.setStage(MatchStageEnum.ONGOING);

                            for (TeamPlayer tp : match.getMembers()) {

                                Player p = Bukkit.getPlayer(tp.uuid());

                                if (p == null) continue;

                                String startSound =
                                        Solar.configManager
                                                .settingsRecord()
                                                .soundRecords()
                                                .startSound();

                                SoundUtils.playConfiguredSound(
                                        p, startSound, Sound.BLOCK_BEACON_ACTIVATE);

                                p.sendMessage(
                                        Solar.miniMessage.deserialize(
                                                Solar.configManager.languageRecord().matchBegin(),
                                                Placeholder.parsed(
                                                        "prefix",
                                                        Solar.configManager
                                                                .languageRecord()
                                                                .prefix())));
                            }
                        },
                        seconds * 20L);
    }
}
