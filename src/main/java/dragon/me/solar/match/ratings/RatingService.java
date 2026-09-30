package dragon.me.solar.match.ratings;

import dragon.me.solar.Solar;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.queue.InMemoryQueue;
import dragon.me.solar.utils.MathUtils;
import org.bukkit.Bukkit;

public class RatingService {

    public static void updateRating(InMemoryTeam winner, InMemoryTeam loser, InMemoryQueue queue) {
        int winnerTeamRating = winner.getAvgRating(queue);
        int loserTeamRating = loser.getAvgRating(queue);

        for (TeamPlayer wp : winner.getMembers()) {

            Solar.cache.getQueue(wp.uuid(), queue.name).thenAccept(queueStat -> {
                int oldRating = queueStat.getElo();

                int newRating =
                        MathUtils.calculateNewRating(queueStat.getElo(), winnerTeamRating, loserTeamRating, 1.0);

                queueStat.setElo(newRating);
                Solar.databaseManager.updateQueueStat(wp.uuid(), queue.name, queueStat);

                Solar.instance
                        .getLogger()
                        .info("[DEBUG] " + Bukkit.getPlayer(wp.uuid()) + "'s ELO: " + oldRating + " -> " + newRating);
            });
        }

        for (TeamPlayer lp : loser.getMembers()) {

            Solar.cache.getQueue(lp.uuid(), queue.name).thenAccept(queueStat -> {
                int oldRating = queueStat.getElo();
                int newRating =
                        MathUtils.calculateNewRating(queueStat.getElo(), loserTeamRating, winnerTeamRating, 0.0);

                queueStat.setElo(newRating);
                Solar.databaseManager.updateQueueStat(lp.uuid(), queue.name, queueStat);

                Solar.instance
                        .getLogger()
                        .info("[DEBUG] " + Bukkit.getPlayer(lp.uuid()) + "'s ELO: " + oldRating + " -> " + newRating);
            });
        }
    }
}
