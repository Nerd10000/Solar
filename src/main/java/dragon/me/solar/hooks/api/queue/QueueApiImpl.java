package dragon.me.solar.hooks.api.queue;

import dragon.me.solar.api.queue.Queue;
import dragon.me.solar.api.queue.QueueAPI;
import dragon.me.solar.queue.QueueManager;
import java.util.Optional;

public class QueueApiImpl implements QueueAPI {

    private final QueueManager queueManager;

    public QueueApiImpl(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    @Override
    public Optional<Queue> getQueue(String name) {
        return Optional.ofNullable(queueManager.get(name)).map(QueueImpl::new);
    }
}
