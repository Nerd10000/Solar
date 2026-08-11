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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class MatchManager {

    private final List<InMemoryMatch> matchList = new ArrayList<>();

    public MatchManager() {}

    public List<InMemoryMatch> getMatchList() {
        return matchList;
    }

    public void remove(UUID uuid) {
        matchList.removeIf(match -> match.getUuid().equals(uuid));
    }

    public void add(InMemoryMatch match) {
        matchList.add(match);
    }

    public @Nullable InMemoryMatch getMatchByMember(UUID uuid) {

        for (InMemoryMatch match : matchList) {

            for (InMemoryTeam team : match.getTeamList()) {

                for (TeamPlayer player : team.getMembers()) {

                    if (player.uuid().equals(uuid)) {

                        return match;
                    }
                }
            }
        }
        return null;
    }

    public boolean isPlayerInAMatch(UUID playerId) {

        for (InMemoryMatch match : matchList) {

            for (InMemoryTeam team : match.getTeamList()) {

                for (TeamPlayer player : team.getMembers()) {

                    if (player.uuid().equals(playerId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public InMemoryMatch startMatch(
            UUID playerOne,
            UUID playerTwo,
            String kitId,
            String mapName,
            String arenaName,
            int gridSlot) {

        InMemoryTeam teamOne =
                new InMemoryTeam(new ArrayList<>(List.of(new TeamPlayer(playerOne, true))), true);
        InMemoryTeam teamTwo =
                new InMemoryTeam(new ArrayList<>(List.of(new TeamPlayer(playerTwo, true))), true);

        InMemoryMatch match =
                new InMemoryMatch(new ArrayList<>(List.of(teamOne, teamTwo)), kitId, mapName);
        match.setArenaName(arenaName);
        match.setGridSlot(gridSlot);
        // Mark match as starting and run a short countdown before making it ongoing.
        match.setStage(MatchStageEnum.STARTING);

        InMemoryKit kit = Solar.kitManager.getKit(kitId);

        for (InMemoryTeam team : match.getTeamList()) {
            for (TeamPlayer teamPlayer : team.getMembers()) {
                Player player = Bukkit.getPlayer(teamPlayer.uuid());
                if (player == null) {
                    continue;
                }

                match.saveInventory(player);

                if (kit != null) {
                    Solar.kitManager.applyKit(player, kit);

                    if (kit.flags.maxHealth() != 20 || kit.flags.maxHealth() != -1) {

                        player.setMaxHealth(kit.flags.maxHealth());
                    }
                }

                player.heal(Integer.MAX_VALUE);
                player.setFoodLevel(20);
            }
        }

        add(match);

        final int countdownSeconds = 3;

        for (int i = 0; i < countdownSeconds; i++) {
            final int secondsLeft = countdownSeconds - i;
            Bukkit.getScheduler()
                    .runTaskLater(
                            Solar.instance,
                            () -> {
                                String sound =
                                        Solar.configManager
                                                .settingsRecord()
                                                .soundRecords()
                                                .countdownSound();
                                executeForEachMember(
                                        player ->
                                                SoundUtils.playConfiguredSound(
                                                        player,
                                                        sound,
                                                        Sound.BLOCK_NOTE_BLOCK_PLING),
                                        match);

                                match.broadcast(
                                        Solar.miniMessage.deserialize(
                                                "<prefix><gray>Match starts in <color:#FCD05C>"
                                                        + secondsLeft
                                                        + "</color>...</gray>",
                                                Placeholder.parsed(
                                                        "prefix",
                                                        Solar.configManager
                                                                .languageRecord()
                                                                .prefix())));
                            },
                            i * 20L);
        }

        Bukkit.getScheduler()
                .runTaskLater(
                        Solar.instance,
                        () -> {
                            match.setStage(MatchStageEnum.ONGOING);
                            executeForEachMember(
                                    player ->
                                            SoundUtils.playConfiguredSound(
                                                    player,
                                                    Solar.configManager
                                                            .settingsRecord()
                                                            .soundRecords()
                                                            .startSound(),
                                                    Sound.BLOCK_BEACON_ACTIVATE),
                                    match);
                            match.broadcast(
                                    Solar.miniMessage.deserialize(
                                            Solar.configManager.languageRecord().matchBegin(),
                                            Placeholder.parsed(
                                                    "prefix",
                                                    Solar.configManager
                                                            .languageRecord()
                                                            .prefix())));
                        },
                        countdownSeconds * 20L);

        return match;
    }

    public void endMatch(
            InMemoryMatch match, @Nullable InMemoryTeam winner, MatchEndReason reason) {

        if (match.getStage() == MatchStageEnum.ENDED) {
            return;
        }

        match.setStage(MatchStageEnum.ENDED);

        if (winner != null) {
            match.setWinner(winner);
        }

        executeForEachMember(p -> p.getInventory().clear(), match);
        String winnerName = resolveWinnerName(winner);
        announceResults(match, winner, winnerName, reason);
        restoreAndTeleportPlayers(match);

        int gridSlot = match.getGridSlot();
        String arenaName = match.getArenaName();

        match.getSavedInventories().clear();

        executeForEachMember(
                consumer -> {
                    consumer.setMaxHealth(20);
                },
                match);

        if (gridSlot >= 0) {
            Solar.gridManager.free(gridSlot);
        }

        if (arenaName != null && gridSlot >= 0) {
            BlockVector3 center = Solar.gridManager.getCenter(gridSlot);
            Bukkit.getScheduler()
                    .runTaskLater(
                            Solar.instance,
                            () -> {
                                FaweHook.clearArena(arenaName, center);
                                remove(match.getUuid());
                            },
                            5 * 20L);
        }
    }

    public void endMatchInstant(
            InMemoryMatch match, @Nullable InMemoryTeam winner, MatchEndReason reason) {

        if (match.getStage() == MatchStageEnum.ENDED) {
            return;
        }

        match.setStage(MatchStageEnum.ENDED);

        if (winner != null) {
            match.setWinner(winner);
        }

        executeForEachMember(p -> p.getInventory().clear(), match);
        String winnerName = resolveWinnerName(winner);
        announceResults(match, winner, winnerName, reason);
        restoreAndTeleportPlayersInstant(match);

        int gridSlot = match.getGridSlot();
        String arenaName = match.getArenaName();

        match.getSavedInventories().clear();

        executeForEachMember(
                consumer -> {
                    consumer.setMaxHealth(20);
                },
                match);

        if (gridSlot >= 0) {
            Solar.gridManager.free(gridSlot);
        }

        if (arenaName != null && gridSlot >= 0) {
            BlockVector3 center = Solar.gridManager.getCenter(gridSlot);
            FaweHook.clearArenaSync(arenaName, center);
        }

        remove(match.getUuid());
    }

    private void announceResults(
            InMemoryMatch match,
            @Nullable InMemoryTeam winner,
            String winnerName,
            MatchEndReason reason) {

        for (InMemoryTeam team : match.getTeamList()) {
            boolean isWinner = winner != null && team == winner;

            for (TeamPlayer teamPlayer : team.getMembers()) {
                Player player = Bukkit.getPlayer(teamPlayer.uuid());
                if (player == null) {
                    continue;
                }

                if (reason == MatchEndReason.FORFEIT) {
                    player.sendMessage(
                            Solar.miniMessage.deserialize(
                                    isWinner
                                            ? Solar.configManager.languageRecord().matchForfeitWin()
                                            : Solar.configManager
                                                    .languageRecord()
                                                    .matchForfeitLoss(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("winner", winnerName)));
                    continue;
                }

                if (isWinner) {
                    SoundUtils.playConfiguredSound(
                            player,
                            Solar.configManager.settingsRecord().soundRecords().wonSound(),
                            Sound.UI_TOAST_CHALLENGE_COMPLETE);
                } else {
                    SoundUtils.playConfiguredSound(
                            player,
                            Solar.configManager.settingsRecord().soundRecords().lostSound(),
                            Sound.BLOCK_BEACON_DEACTIVATE);
                }

                player.sendMessage(
                        Solar.miniMessage.deserialize(
                                isWinner
                                        ? Solar.configManager.languageRecord().matchWon()
                                        : Solar.configManager.languageRecord().matchLost(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix()),
                                Placeholder.parsed("winner", winnerName)));
            }
        }
    }

    private void restoreAndTeleportPlayers(InMemoryMatch match) {
        Location lobby = Solar.configManager.getLobbyLocation();

        for (InMemoryTeam team : match.getTeamList()) {
            for (TeamPlayer teamPlayer : team.getMembers()) {
                Player player = Bukkit.getPlayer(teamPlayer.uuid());
                if (player == null) {
                    continue;
                }

                if (player.isDead()) {
                    Bukkit.getScheduler()
                            .runTaskLater(
                                    Solar.instance,
                                    () -> {
                                        player.spigot().respawn();
                                    },
                                    1L);
                }

                PlayerSnapshot snapshot = match.getSavedInventories().get(teamPlayer.uuid());
                if (snapshot != null) {
                    snapshot.restore(player);
                }

                if (lobby != null) {
                    long delayTicks = 5 * 20L; // 5 seconds
                    Bukkit.getScheduler()
                            .runTaskLater(
                                    Solar.instance,
                                    () -> {
                                        if (player.isOnline()) {
                                            player.teleport(lobby);
                                            player.setInvulnerable(false);
                                            player.setAllowFlight(false);
                                            player.setFireTicks(0);
                                            player.heal(Integer.MAX_VALUE);
                                            player.setFoodLevel(20);
                                        }
                                    },
                                    delayTicks);
                }
            }
        }
    }

    private void restoreAndTeleportPlayersInstant(InMemoryMatch match) {
        Location lobby = Solar.configManager.getLobbyLocation();

        for (InMemoryTeam team : match.getTeamList()) {
            for (TeamPlayer teamPlayer : team.getMembers()) {
                Player player = Bukkit.getPlayer(teamPlayer.uuid());
                if (player == null) {
                    continue;
                }

                if (player.isDead()) {
                    player.spigot().respawn();
                }

                PlayerSnapshot snapshot = match.getSavedInventories().get(teamPlayer.uuid());
                if (snapshot != null) {
                    snapshot.restore(player);
                }

                if (lobby != null && player.isOnline()) {
                    player.teleport(lobby);
                    player.setInvulnerable(false);
                    player.setAllowFlight(false);
                    player.setFireTicks(0);
                    player.heal(Integer.MAX_VALUE);
                    player.setFoodLevel(20);
                }
            }
        }
    }

    private String resolveWinnerName(@Nullable InMemoryTeam winner) {
        if (winner == null) {
            return "Unknown";
        }

        Iterator<TeamPlayer> iterator = winner.getMembers().iterator();
        if (!iterator.hasNext()) {
            return "Unknown";
        }

        Player player = Bukkit.getPlayer(iterator.next().uuid());
        return player != null ? player.getName() : "Unknown";
    }

    public void executeForEachMember(Consumer<Player> consumer, InMemoryMatch match) {

        for (InMemoryTeam team : match.getTeamList()) {

            for (TeamPlayer player : team.getMembers()) {
                Player onlinePlayer = Bukkit.getPlayer(player.uuid());
                if (onlinePlayer == null) {
                    continue;
                }
                consumer.accept(onlinePlayer);
            }
        }
    }

    public void terminateMatch(InMemoryMatch match) {

        endMatch(match, null, MatchEndReason.TERMINATED);
    }
}
