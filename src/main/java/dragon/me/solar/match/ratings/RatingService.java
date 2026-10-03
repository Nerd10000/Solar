package dragon.me.solar.match.ratings;

import dragon.me.solar.Solar;
import dragon.me.solar.database.models.queue.QueueStat;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.queue.InMemoryQueue;
import dragon.me.solar.utils.MathUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RatingService {

    public static CompletableFuture<Void> updateRating(InMemoryTeam winner, InMemoryTeam loser, InMemoryQueue queue) {
        List<UUID> winnerPlayers =
                winner.getMembers().stream().map(TeamPlayer::uuid).toList();

        List<UUID> loserPlayers =
                loser.getMembers().stream().map(TeamPlayer::uuid).toList();

        List<UUID> players =
                Stream.concat(winnerPlayers.stream(), loserPlayers.stream()).toList();

        List<CompletableFuture<QueueStat>> futures = players.stream()
                .map(uuid -> Solar.cache.getQueue(uuid, queue.name))
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenRun(() -> {
                    List<QueueStat> stats =
                            futures.stream().map(CompletableFuture::join).toList();

                    Map<UUID, QueueStat> statsByPlayer = new HashMap<>();

                    for (int i = 0; i < players.size(); i++) {
                        statsByPlayer.put(players.get(i), stats.get(i));
                    }

                    double winnerTeamRating = winner.getMembers().stream()
                            .mapToDouble(
                                    player -> statsByPlayer.get(player.uuid()).getElo())
                            .average()
                            .orElse(0);

                    double loserTeamRating = loser.getMembers().stream()
                            .mapToDouble(
                                    player -> statsByPlayer.get(player.uuid()).getElo())
                            .average()
                            .orElse(0);

                    for (TeamPlayer player : winner.getMembers()) {
                        QueueStat stat = statsByPlayer.get(player.uuid());

                        int oldRating = stat.getElo();

                        int newRating = MathUtils.calculateNewRating(
                                oldRating, (int) winnerTeamRating, (int) loserTeamRating, 1.0);

                        stat.setElo(newRating);

                        Solar.databaseManager.updateQueueStat(player.uuid(), queue.name, stat);

                        Solar.instance
                                .getLogger()
                                .info("[DEBUG] " + player.uuid() + "'s ELO: " + oldRating + " -> " + newRating);
                        sendRatingChange(player.uuid(), queue.name, oldRating, newRating);
                    }

                    for (TeamPlayer player : loser.getMembers()) {
                        QueueStat stat = statsByPlayer.get(player.uuid());

                        int oldRating = stat.getElo();

                        int newRating = MathUtils.calculateNewRating(
                                oldRating, (int) loserTeamRating, (int) winnerTeamRating, 0.0);

                        stat.setElo(newRating);

                        Solar.databaseManager.updateQueueStat(player.uuid(), queue.name, stat);
                        sendRatingChange(player.uuid(), queue.name, oldRating, newRating);

                        Solar.instance
                                .getLogger()
                                .info("[DEBUG] " + player.uuid() + "'s ELO: " + oldRating + " -> " + newRating);
                    }
                });
    }

    private static void sendRatingChange(UUID uuid, String queue, int oldRating, int newRating) {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return;
        }

        int change = newRating - oldRating;

        String status = change >= 0 // TODO customize this!
                ? "gained"
                : "lost";

        Solar.messageService.send(
                player,
                Solar.messageService.language().eloChange(),
                Placeholder.parsed("queue", queue),
                Placeholder.parsed("status", status),
                Placeholder.parsed("change", String.valueOf(Math.abs(change))),
                Placeholder.parsed("old", String.valueOf(oldRating)),
                Placeholder.parsed("new", String.valueOf(newRating)));
    }
}
