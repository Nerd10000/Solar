package dragon.me.solar.api.events;

import dragon.me.solar.api.match.Match;
import dragon.me.solar.api.match.MatchEndReason;
import dragon.me.solar.api.team.Team;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class MatchEndEvent extends MatchEvent {

    private static final HandlerList handlers = new HandlerList();

    private final MatchEndReason reason;
    private final Team winner;

    public MatchEndEvent(Match match, MatchEndReason reason, Team winner) {
        super(match);

        this.reason = reason;
        this.winner = winner;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public MatchEndReason getReason() {
        return reason;
    }

    public Team getWinner() {
        return winner;
    }
}
