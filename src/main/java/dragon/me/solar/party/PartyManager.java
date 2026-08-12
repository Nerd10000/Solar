package dragon.me.solar.party;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class PartyManager {

    private List<InMemoryParty> parties = new ArrayList<>();

    public PartyManager() {}

    public boolean isMemberOfAParty(UUID uuid) {

        for (InMemoryParty p : parties) {

            if (p.getMemberList().contains(uuid)) return true;
        }
        return false;
    }

    public boolean isOwnerOfParty(InMemoryParty party, UUID uuid) {
        return party.getOwner().equals(uuid);
    }

    public void transferOwnership(InMemoryParty party, UUID uuid) {
        party.setOwner(uuid);
    }

    public void addParty(InMemoryParty party) {

        parties.add(party);
    }

    public void executeForEachMember(InMemoryParty party, Consumer<Player> task) {

        for (UUID u : party.getMemberList()) {

            Player p = Bukkit.getPlayer(u);

            if (p == null) {
                continue;
            }

            task.accept(p);
        }
    }

    public @Nullable InMemoryParty getByOwner(UUID u) {

        for (InMemoryParty p : parties) {

            if (p.getOwner().equals(u)) {
                return p;
            }
        }
        return null;
    }

    public void removeParty(InMemoryParty p) {
        parties.remove(p);
    }
}
