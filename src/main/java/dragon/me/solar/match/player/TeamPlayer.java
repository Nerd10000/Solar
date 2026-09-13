package dragon.me.solar.match.player;

import java.util.UUID;

public record TeamPlayer(UUID uuid, boolean isAlive) {

    public TeamPlayer updateStatus(boolean isAlive) {

        TeamPlayer newPlayer = new TeamPlayer(this.uuid, isAlive);
        return newPlayer;
    }

    public static TeamPlayer fromUuid(UUID uuid) {

        return new TeamPlayer(uuid, true);
    }
}
