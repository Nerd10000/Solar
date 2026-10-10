package dragon.me.solar.hooks.api.queue;

import dragon.me.solar.api.queue.Queue;
import dragon.me.solar.api.queue.QueueFlags;
import dragon.me.solar.queue.InMemoryQueue;
import java.util.UUID;

public class QueueImpl implements Queue {

    private final InMemoryQueue handle;

    public QueueImpl(InMemoryQueue handle) {
        this.handle = handle;
    }

    @Override
    public String getName() {
        return handle.name;
    }

    @Override
    public String getKit() {
        return handle.kitId;
    }

    @Override
    public java.util.Queue<UUID> getPlayers() {
        return handle.queue;
    }

    @Override
    public boolean isEnabled() {
        return handle.isEnabled;
    }

    @Override
    public QueueFlags getFlags() {
        return new QueueFlagsImpl(handle.flags);
    }
}
