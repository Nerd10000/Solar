package dragon.me.solar.api.stats;

import java.util.UUID;

/**
 * Allows you to access statistics about the players.
 */
public interface PlayerStatsAPI {

    /**
     *
     * @param player the UUID of the target player.
     * @param kit the name of the kit.
     * @return how many wins the player have with that kit, if the kit or the player is invalid it will return 0.
     */
    int getWins(UUID player, String kit);

    /**
     *
     * @param player the UUID of the target player.
     * @param kit the name of the kit.
     * @return how many losses the player have with that kit, if the kit or the player is invalid it will return 0.
     */
    int getLosses(UUID player, String kit);

    /**
     *
     * @param player the UUID of the target player.
     * @param kit the name of the kit.
     * @return how much elo the player have with that kit, if the kit or the player is invalid it will return the default which is specified in the config.
     */
    int getElo(UUID player, String kit);
}
