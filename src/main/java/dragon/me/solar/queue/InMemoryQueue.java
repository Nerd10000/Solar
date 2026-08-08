package dragon.me.solar.queue;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;

public class InMemoryQueue {

    public final String kitId;
    public Queue<UUID> queue = new ArrayDeque<>();

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

    public UUID[] getNextParticipants() {

        UUID u1 = queue.remove();
        UUID u2 = queue.remove();

        return new UUID[] {u1, u2};
    }

    public boolean isEnoughForNextMatch() {

        return queue.size() >= 2;
    }
}
