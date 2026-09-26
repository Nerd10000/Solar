package dragon.me.solar.queue;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

public class InMemoryQueue {

    public final String kitId;
    public Queue<UUID> queue = new ArrayDeque<>();
    public int teamSize = 1;

    public InMemoryQueue(String kitId) {

        this.kitId = kitId;
    }

    public void join(UUID uuid) {

        queue.add(uuid);
    }

    public boolean inQueue(UUID uuid) {

        return queue.contains(uuid);
    }

    public void leave(UUID uuid) {

        queue.remove(uuid);
    }

    public UUID[] getNextParticipants(int amount) {
        if (queue.size() < amount) {
            return new UUID[0];
        }

        UUID[] results = new UUID[amount];

        for (int i = 0; i < amount; i++) {
            results[i] = queue.poll();
        }

        return results;
    }

    public boolean isEnoughForNextMatch(int teamSize) {

        return queue.size() >= teamSize * 2;
    }
}
