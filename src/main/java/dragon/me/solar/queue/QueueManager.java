package dragon.me.solar.queue;

import dragon.me.solar.configs.records.QueueRecord;
import dragon.me.solar.configs.records.QueuesRecord;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class QueueManager {

    public Map<String, InMemoryQueue> queueManagerMap = new HashMap<>();

    public QueueManager() {}

    public void load(QueuesRecord queues) {

        for (Map.Entry<String, QueueRecord> entry : queues.queues().entrySet()) {

            InMemoryQueue queue =
                    new InMemoryQueue(entry.getKey(), entry.getValue().kit());
            queue.teamSize = entry.getValue().teamSize();
            queue.flags = entry.getValue().flags();
            queue.isEnabled = entry.getValue().isEnabled();
        }
    }

    public @Nullable InMemoryQueue get(String queueName) {

        return queueManagerMap.get(queueName);
    }

    public boolean create(InMemoryQueue queue) {

        if (queueManagerMap.containsKey(queue.name)) {
            return false;
        }

        queueManagerMap.put(queue.name, queue);

        return true;
    }
}
