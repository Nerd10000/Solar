package dragon.me.solar.party.invite;

import java.util.UUID;

public record PartyInviteRecord(UUID sender, UUID receiver, UUID partyUuid, long timestamp) {}
