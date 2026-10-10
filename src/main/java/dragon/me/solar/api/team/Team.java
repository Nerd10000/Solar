package dragon.me.solar.api.team;

import java.util.List;

/**
 *
 * Represents a team who is part of a match.
 *
 */
public interface Team {

    /**
     * Returns all players in a match.
     */
    List<TeamPlayer> getPlayers();

    /**
     * Get the number of rounds that the team won in the match.
     */
    int getRoundWins();

    /**
     * Returns 'true' if at-least 1 player alive in the team. (The team is alive)
     */
    boolean isAlive();

    /**
     * @param queue name of the queue
     *  Returns the average rating of the team. Calculated from all the members of the team and averaged.
     */
    int getRating(String queue);
}
