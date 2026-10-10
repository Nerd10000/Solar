package dragon.me.solar.api.queue;

import java.util.UUID;

/**
 * Represents a queue.
 */
public interface Queue {

    /**
     *
     * @return the name of the queue.
     */
    String getName();

    /**
     *
     * @return the kit that the queue is paired with.
     */
    String getKit();

    /**
     *
     * @return all the players that are in the queue.
     */
    java.util.Queue<UUID> getPlayers();

    /**
     *
     * @return 'true' if the queue is enabled.
     */
    boolean isEnabled();

    /**
     *
     * @return the flags for this queue.
     * @see QueueFlags
     */
    QueueFlags getFlags();
}
