package dragon.me.solar.api.events;

import dragon.me.solar.api.match.Match;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class MatchStartEvent extends MatchEvent {

    private static final HandlerList handlers = new HandlerList();

    public MatchStartEvent(Match match) {
        super(match);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
