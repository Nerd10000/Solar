package dragon.me.solar.hooks.api.stats;

import dragon.me.solar.api.stats.PlayerStatsAPI;
import dragon.me.solar.database.PlayerCache;
import java.util.UUID;

public class PlayerStatsApiImpl implements PlayerStatsAPI {

    private final PlayerCache cache;

    public PlayerStatsApiImpl(PlayerCache cache) {
        this.cache = cache;
    }

    @Override
    public int getWins(UUID player, String kit) {
        return cache.getWins(player, kit);
    }

    @Override
    public int getLosses(UUID player, String kit) {
        return cache.getLosses(player, kit);
    }

    @Override
    public int getElo(UUID player, String kit) {
        return cache.getElo(player, kit);
    }
}
