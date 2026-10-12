package dragon.me.solar.hooks.api;

import dragon.me.solar.Solar;
import dragon.me.solar.api.SolarAPI;
import dragon.me.solar.api.match.MatchAPI;
import dragon.me.solar.api.queue.QueueAPI;
import dragon.me.solar.api.stats.PlayerStatsAPI;
import dragon.me.solar.hooks.api.match.MatchApiImpl;
import dragon.me.solar.hooks.api.queue.QueueApiImpl;
import dragon.me.solar.hooks.api.stats.PlayerStatsApiImpl;

public final class SolarApiImpl implements SolarAPI {

    private final MatchAPI matchAPI;
    private final QueueAPI queueAPI;
    private final PlayerStatsAPI playerStatsAPI;

    public SolarApiImpl() {
        this.matchAPI = new MatchApiImpl(Solar.matchManager);
        this.queueAPI = new QueueApiImpl(Solar.queueManager);
        this.playerStatsAPI = new PlayerStatsApiImpl(Solar.cache);
    }

    @Override
    public MatchAPI getMatchAPI() {
        return matchAPI;
    }

    @Override
    public QueueAPI getQueueAPI() {
        return queueAPI;
    }

    @Override
    public PlayerStatsAPI getPlayerStatsAPI() {
        return playerStatsAPI;
    }
}
