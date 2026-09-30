package dragon.me.solar.database;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dragon.me.solar.Solar;
import dragon.me.solar.database.models.PlayerStat;
import dragon.me.solar.database.models.StatKey;
import dragon.me.solar.database.models.queue.QueueKey;
import dragon.me.solar.database.models.queue.QueueStat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerCache {

    private final AsyncLoadingCache<StatKey, PlayerStat> statCache;
    private final AsyncLoadingCache<QueueKey, QueueStat> queueCache;

    public PlayerCache() {

        this.statCache = Caffeine.newBuilder().maximumSize(10_000).buildAsync((key, executor) -> Solar.databaseManager
                .getStatById(key.uuid(), key.kit())
                .thenApply(stat -> {
                    if (stat == null) {

                        stat = new PlayerStat();

                        stat.setUuid(key.uuid().toString());
                        stat.setKit(key.kit());
                    }
                    return stat;
                }));

        this.queueCache = Caffeine.newBuilder().maximumSize(10_000).buildAsync((key, executor) -> Solar.databaseManager
                .getQueueStat(key.uuid(), key.queue())
                .thenApply(queue -> {
                    if (queue == null) {

                        queue = new QueueStat();

                        queue.setUuid(key.uuid().toString());
                        queue.setQueueId(key.queue());
                    }
                    return queue;
                }));
    }

    public CompletableFuture<PlayerStat> getPlayer(UUID uuid, String kit) {
        return statCache.get(new StatKey(uuid, kit));
    }

    public CompletableFuture<QueueStat> getQueue(UUID uuid, String kit) {
        return queueCache.get(new QueueKey(uuid, kit));
    }

    public int getElo(UUID uuid, String queue) {

        QueueKey queueKey = new QueueKey(uuid, queue);
        queueCache.get(queueKey);

        QueueStat stat = queueCache.synchronous().get(queueKey);

        if (stat != null) {
            return stat.getElo();
        }
        return -1;
    }

    public List<PlayerStat> getPlayerAll(UUID uuid) {

        return statCache.synchronous().asMap().values().stream()
                .filter(stat -> stat.getUuid().equals(uuid.toString()))
                .toList();
    }

    public void invalidatePlayerAll(UUID uuid) {
        statCache.synchronous().asMap().keySet().removeIf(key -> key.uuid().equals(uuid));
    }

    public int getWins(UUID uuid, String kit) {
        StatKey key = new StatKey(uuid, kit);
        statCache.get(key);
        PlayerStat stat = statCache.synchronous().getIfPresent(key);

        if (stat != null) {
            return stat.getWins();
        }

        return -1;
    }

    public int getLosses(UUID uuid, String kit) {
        StatKey key = new StatKey(uuid, kit);
        statCache.get(key);
        PlayerStat stat = statCache.synchronous().getIfPresent(key);

        if (stat != null) {
            return stat.getLosses();
        }

        return -1;
    }

    public void invalidate(UUID uuid, String kit) {
        statCache.synchronous().invalidate(new StatKey(uuid, kit));
    }
}
