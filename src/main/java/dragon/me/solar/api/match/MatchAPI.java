package dragon.me.solar.api.match;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * MatchAPI is the manager of matches.
 *
 */
public interface MatchAPI {

    /**
     * Gets the match by its UUID
     * @return an optional which contains the match, or if it is not found returns an empty optional.
     */
    Optional<Match> getMatch(UUID uuid);

    /**
     *
     * @param uuid The uuid of the player.
     * @return an optional which contains the match, or if it is not found returns an empty optional.
     */
    Optional<Match> getMatchByPlayer(UUID uuid);
}
