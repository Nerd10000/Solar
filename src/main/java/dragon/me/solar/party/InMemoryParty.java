package dragon.me.solar.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InMemoryParty {

    private UUID uuid;
    private UUID owner;
    private List<UUID> memberList = new ArrayList<>();

    public InMemoryParty(UUID uuid, UUID playerId) {
        this.uuid = uuid;
        this.owner = playerId;
        memberList.add(owner);
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMemberList() {
        return memberList;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setMemberList(List<UUID> memberList) {
        this.memberList = memberList;
    }
}
