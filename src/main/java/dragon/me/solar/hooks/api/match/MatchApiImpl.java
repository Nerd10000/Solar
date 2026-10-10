package dragon.me.solar.hooks.api.match;

import dragon.me.solar.api.match.Match;
import dragon.me.solar.api.match.MatchAPI;
import dragon.me.solar.match.MatchManager;
import java.util.Optional;
import java.util.UUID;

public final class MatchApiImpl implements MatchAPI {

    private final MatchManager matchManager;

    public MatchApiImpl(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @Override
    public Optional<Match> getMatch(UUID uuid) {
        return matchManager.getMatchList().stream()
                .filter(match -> match.getUuid().equals(uuid))
                .findFirst()
                .map(MatchImpl::new);
    }

    @Override
    public Optional<Match> getMatchByPlayer(UUID uuid) {
        return Optional.ofNullable(matchManager.getMatchByMember(uuid)).map(MatchImpl::new);
    }
}
