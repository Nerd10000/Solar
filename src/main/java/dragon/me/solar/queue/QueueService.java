package dragon.me.solar.queue;

import dragon.me.solar.match.MatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;

public class QueueService {

    private final QueueManager queueManager;

    public QueueService(QueueManager queueManager) {

        this.queueManager = queueManager;
    }

    public void joinQueue(UUID playerId, String kit) {

        InMemoryQueue queue = queueManager.get(kit);

        if (queue == null) {
            return;
        }

        queue.join(playerId);
    }

    public void leaveQueue(UUID playerId, String kit) {

        InMemoryQueue queue = queueManager.get(kit);

        if (queue == null) {
            return;
        }

        queue.leave(playerId);
    }

    public void startMatch(Player p, String kit, MatchService service) {

        InMemoryQueue queue = queueManager.get(kit);

        if (queue != null) {

            if (!queue.isEnoughForNextMatch(queue.teamSize)) {
                return;
            }
            List<UUID> players = Arrays.asList(queue.getNextParticipants(queue.teamSize * 2));

            List<List<UUID>> teams = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                int from = i * queue.teamSize;
                int to = from + queue.teamSize;

                teams.add(new ArrayList<>(players.subList(from, to)));
            }
        }
    }
}
