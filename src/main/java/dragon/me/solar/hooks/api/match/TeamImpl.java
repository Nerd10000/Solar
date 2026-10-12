package dragon.me.solar.hooks.api.match;

import dragon.me.solar.Solar;
import dragon.me.solar.api.team.Team;
import dragon.me.solar.api.team.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import java.util.List;
import java.util.Objects;

public class TeamImpl implements Team {

    private final InMemoryTeam handle;

    public TeamImpl(InMemoryTeam handle) {
        this.handle = handle;
    }

    @Override
    public List<TeamPlayer> getPlayers() {
        return handle.getMembers().stream().<TeamPlayer>map(TeamPlayerImpl::new).toList();
    }

    @Override
    public int getRoundWins() {
        return handle.getRoundWins();
    }

    @Override
    public boolean isAlive() {
        return handle.isTeamAlive();
    }

    @Override
    public int getRating(String queue) {
        return handle.getAvgRating(Objects.requireNonNull(Solar.queueManager.get(queue)));
    }
}
