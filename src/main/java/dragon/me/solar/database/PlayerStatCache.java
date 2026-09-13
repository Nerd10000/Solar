package dragon.me.solar.database;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import dragon.me.solar.Solar;
import dragon.me.solar.database.models.PlayerStat;
import dragon.me.solar.database.models.StatKey;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStatCache {

    private final AsyncLoadingCache<StatKey, PlayerStat> cache;

    public PlayerStatCache() {

        this.cache =
                Caffeine.newBuilder()
                        .maximumSize(10_000)
                        .buildAsync(
                                (key, executor) ->
                                        Solar.databaseManager
                                                .getStatById(key.uuid(), key.kit())
                                                .thenApply(
                                                        stat -> {
                                                            if (stat == null) {

                                                                stat = new PlayerStat();

                                                                stat.setUuid(key.uuid().toString());
                                                                stat.setKit(key.kit());
                                                            }
                                                            return stat;
                                                        }));
    }

    public CompletableFuture<PlayerStat> get(UUID uuid, String kit) {
        return cache.get(new StatKey(uuid, kit));
    }

    public List<PlayerStat> getAll(UUID uuid) {

        return cache.synchronous().asMap().values().stream()
                .filter(stat -> stat.getUuid().equals(uuid.toString()))
                .toList();
    }

    public void invalidateAll(UUID uuid) {
        cache.synchronous().asMap().keySet().removeIf(key -> key.uuid().equals(uuid));
    }

    public void invalidate(UUID uuid, String kit) {
        cache.synchronous().invalidate(new StatKey(uuid, kit));
    }
}
