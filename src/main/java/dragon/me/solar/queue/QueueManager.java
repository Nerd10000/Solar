package dragon.me.solar.queue;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class QueueManager {

    public Map<String, InMemoryQueue> queueManagerMap = new HashMap<>();

    public QueueManager() {}

    public void loadAll(InMemoryQueue... queues) {

        for (InMemoryQueue queue : queues) {

            queueManagerMap.putIfAbsent(queue.kitId, queue);
        }
    }

    public @Nullable InMemoryQueue get(String kitId) {

        return queueManagerMap.get(kitId);
    }
}
