package dragon.me.solar.api.match;

import dragon.me.solar.api.team.Team;
import java.util.List;
import java.util.UUID;

/**
 *
 * Represents a match.
 *
 */
public interface Match {

    // Gets the identifier of the match.
    UUID getUUID();

    // Gets the status of the match (STARTING, ONGOING, ENDED).
    MatchState getMatchState();

    // Gets all the players that are in teams (spectators are excluded).
    List<UUID> getPlayers();

    // Gets the kit of the match.
    String getKit();

    // Gets the map/arena of the match.
    String getArena();

    // Gets all the spectators of the match.
    List<UUID> getSpectators();

    // Gets how many round does one team need to win to end the match.
    int getRounds();

    // Gets the number of the current round.
    int getCurrentRound();

    // Returns 'true' if the match is an FFA type.
    boolean isFFA();

    // Gets the time when the match started in milliseconds.
    long getStartTimeMillis();

    // TODO: getter for teams, origin

    // Gets all the teams that are in the match.
    List<Team> getTeams();
}
