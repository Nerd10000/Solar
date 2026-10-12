package dragon.me.solar.api.team;

import java.util.UUID;

/**
 *
 * Represents the player who is in a team. (Part of a match)
 *
 */
public interface TeamPlayer {

    /**
     * Gets the UUID of the player.
     */
    UUID getUUID();

    /**
     * Returns true if the player is still alive!
     */
    boolean isAlive();
}
