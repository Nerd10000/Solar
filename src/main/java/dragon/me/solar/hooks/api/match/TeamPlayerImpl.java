package dragon.me.solar.hooks.api.match;

import dragon.me.solar.api.team.TeamPlayer;
import java.util.UUID;

public class TeamPlayerImpl implements TeamPlayer {

    private final dragon.me.solar.match.player.TeamPlayer handle;

    public TeamPlayerImpl(dragon.me.solar.match.player.TeamPlayer handle) {
        this.handle = handle;
    }

    @Override
    public UUID getUUID() {
        return handle.uuid();
    }

    @Override
    public boolean isAlive() {
        return handle.isAlive();
    }
}
