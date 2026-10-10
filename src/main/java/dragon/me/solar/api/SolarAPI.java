package dragon.me.solar.api;

import dragon.me.solar.api.match.MatchAPI;
import dragon.me.solar.api.queue.QueueAPI;
import dragon.me.solar.api.stats.PlayerStatsAPI;

public interface SolarAPI {
    /**
     *
     * @return the match API.
     * @see MatchAPI
     */
    MatchAPI getMatchAPI();

    /**
     *
     * @return the queue API.
     * @see QueueAPI
     */
    QueueAPI getQueueAPI();

    /**
     *
     * @return the player statistics API.
     * @see PlayerStatsAPI
     */
    PlayerStatsAPI getPlayerStatsAPI();
}
