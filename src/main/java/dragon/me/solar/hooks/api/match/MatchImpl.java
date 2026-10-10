package dragon.me.solar.hooks.api.match;

import dragon.me.solar.api.match.Match;
import dragon.me.solar.api.match.MatchState;
import dragon.me.solar.api.team.Team;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.player.TeamPlayer;
import java.util.List;
import java.util.UUID;

public class MatchImpl implements Match {

    private final InMemoryMatch handle;

    public MatchImpl(InMemoryMatch handle) {
        this.handle = handle;
    }

    @Override
    public UUID getUUID() {
        return handle.getUuid();
    }

    @Override
    public MatchState getMatchState() {

        return switch (handle.getStage()) {
            case STARTING -> MatchState.STARTING;
            case ONGOING -> MatchState.ONGOING;
            case ENDED -> MatchState.ENDED;
        };
    }

    @Override
    public List<UUID> getPlayers() {
        return handle.getMembers().stream().map(TeamPlayer::uuid).toList();
    }

    @Override
    public String getKit() {
        return handle.getKit();
    }

    @Override
    public String getArena() {
        return handle.getArenaName();
    }

    @Override
    public List<UUID> getSpectators() {
        return handle.getSpectatorList();
    }

    @Override
    public int getRounds() {
        return handle.getRounds();
    }

    @Override
    public int getCurrentRound() {
        return handle.getCurrentRound();
    }

    @Override
    public boolean isFFA() {
        return handle.isFFA();
    }

    @Override
    public long getStartTimeMillis() {
        return handle.getStartMillis();
    }

    @Override
    public List<Team> getTeams() {
        return handle.getTeamList().stream().<Team>map(TeamImpl::new).toList();
    }
}
