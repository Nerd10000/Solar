package dragon.me.solar.api.events;

import dragon.me.solar.api.match.Match;
import org.bukkit.event.Event;

public abstract class MatchEvent extends Event {

    private final Match match;

    public MatchEvent(Match match) {
        this.match = match;
    }

    public Match getMatch() {
        return match;
    }
}
