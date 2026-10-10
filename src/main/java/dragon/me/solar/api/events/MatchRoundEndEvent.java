package dragon.me.solar.api.events;

import dragon.me.solar.api.match.Match;
import dragon.me.solar.api.team.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class MatchRoundEndEvent extends MatchEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Team winner;

    public MatchRoundEndEvent(Match match, Team winner) {
        super(match);

        this.winner = winner;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }

    public Team getWinner() {
        return winner;
    }
}
