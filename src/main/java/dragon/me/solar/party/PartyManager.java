package dragon.me.solar.party;

import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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

    public @Nullable InMemoryParty getPartyByMember(UUID uuid) {

        for (InMemoryParty p : parties) {

            for (UUID u : p.getMemberList()) {

                if (u.equals(uuid)) {

                    return p;
                }
            }
        }
        return null;
    }

    public void removeParty(InMemoryParty p) {
        parties.remove(p);
    }

    public boolean isSuitableForSplit(InMemoryParty party) {

        return party.getMemberList().size() % 2 == 0;
    }

    public List<InMemoryTeam> generateTeamsForSplit(InMemoryParty party) {

        List<UUID> shuffled = new ArrayList<>(party.getMemberList());
        Collections.shuffle(shuffled);

        int team1Size = (shuffled.size() + 1) / 2;

        List<UUID> team1 = new ArrayList<>(shuffled.subList(0, team1Size));
        List<UUID> team2 = new ArrayList<>(shuffled.subList(team1Size, shuffled.size()));

        InMemoryTeam teamX =
                new InMemoryTeam(
                        team1.stream()
                                .map(uuid -> new TeamPlayer(uuid, true))
                                .collect(Collectors.toCollection(ArrayList::new)),
                        true);

        InMemoryTeam teamY =
                new InMemoryTeam(
                        team2.stream()
                                .map(uuid -> new TeamPlayer(uuid, true))
                                .collect(Collectors.toCollection(ArrayList::new)),
                        true);

        List<InMemoryTeam> teamList = new ArrayList<>();
        teamList.add(teamX);
        teamList.add(teamY);

        return teamList;
    }

    public List<InMemoryParty> getParties() {
        return new ArrayList<>(parties);
    }
}
