package dragon.me.solar.queue;

import dragon.me.solar.configs.records.QueueFlagsRecord;
import dragon.me.solar.configs.records.QueueRecord;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

public class InMemoryQueue {

    public final String kitId;
    public Queue<UUID> queue = new ArrayDeque<>();

    public final String name;
    public QueueFlagsRecord flags;

    public boolean isEnabled = false;

    public InMemoryQueue(String name, String kitId) {
        this.name = name;
        this.kitId = kitId;
    }

    public void join(UUID uuid) {

        if (queue.contains(uuid)) return;
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

    public QueueRecord toRecord() {

        return new QueueRecord(name, kitId, isEnabled, flags);
    }
}
