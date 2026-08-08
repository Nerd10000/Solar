package dragon.me.solar.duel.record;

import java.util.UUID;

public record DuelInviteRecord(
        UUID sender,
        UUID receiver,

        String kit,
        String map,

        int rounds,
        long timestamp
) {

}
