package dragon.me.solar.api.queue;

import java.util.Optional;

public interface QueueAPI {

    /**
     * Gets the queue named 'name'
     * @param name name of the queue
     * @return an optional potentially containing the queue.
     * @see Queue
     */
    Optional<Queue> getQueue(String name);
}
