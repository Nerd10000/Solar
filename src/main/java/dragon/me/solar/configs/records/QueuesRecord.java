package dragon.me.solar.configs.records;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable()
public record QueuesRecord(@Setting("queues") Map<String, QueueRecord> queues) {

    public static final QueuesRecord DEFAULTS = new QueuesRecord(Map.of());

    public QueuesRecord addQueue(QueueRecord queue) {

        HashMap<String, QueueRecord> temp = new HashMap<>(queues());

        temp.put(queue.name(), queue);

        return new QueuesRecord(temp);
    }
}
