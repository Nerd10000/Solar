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
                        queue.setElo(Solar.configManager
                                .settingsRecord()
                                .ratingSettings()
                                .defaultElo());
                    }

                    return queue;
                }));
    }

    /*
     * Async access
     */

    public CompletableFuture<PlayerStat> getPlayer(UUID uuid, String kit) {
        return statCache.get(new StatKey(uuid, kit));
    }

    public CompletableFuture<QueueStat> getQueue(UUID uuid, String queue) {
        return queueCache.get(new QueueKey(uuid, queue));
    }

    /*
     * Synchronous access
     *
     * These are mainly useful for PAPI and other synchronous APIs.
     */

    public PlayerStat getPlayerSync(UUID uuid, String kit) {
        return statCache.synchronous().get(new StatKey(uuid, kit));
    }

    public QueueStat getQueueSync(UUID uuid, String queue) {
        return queueCache.synchronous().get(new QueueKey(uuid, queue));
    }

    public int getElo(UUID uuid, String queue) {

        QueueKey key = new QueueKey(uuid, queue);

        QueueStat stat = queueCache.synchronous().get(key);

        Solar.instance
                .getLogger()
                .info("[ELO DEBUG] uuid=" + uuid
                        + " queue=" + queue
                        + " stat=" + stat
                        + " elo=" + (stat != null ? stat.getElo() : "NULL"));

        return stat != null
                ? stat.getElo()
                : Solar.configManager.settingsRecord().ratingSettings().defaultElo();
    }

    public int getWins(UUID uuid, String kit) {
        return getPlayerSync(uuid, kit).getWins();
    }

    public int getLosses(UUID uuid, String kit) {
        return getPlayerSync(uuid, kit).getLosses();
    }

    /*
     * Player statistics
     */

    public List<PlayerStat> getPlayerAll(UUID uuid) {
        return statCache.synchronous().asMap().values().stream()
                .filter(stat -> stat.getUuid().equals(uuid.toString()))
                .toList();
    }

    public void invalidatePlayerAll(UUID uuid) {
        statCache.synchronous().asMap().keySet().removeIf(key -> key.uuid().equals(uuid));
    }

    public void invalidate(UUID uuid, String kit) {
        statCache.synchronous().invalidate(new StatKey(uuid, kit));
    }

    public void invalidateQueue(UUID uuid, String queue) {
        queueCache.synchronous().invalidate(new QueueKey(uuid, queue));
    }
}
