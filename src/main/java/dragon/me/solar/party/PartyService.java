package dragon.me.solar.party;

import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartyService {
    private final PartyManager partyManager;

    public PartyService(PartyManager partyManager) {
        this.partyManager = partyManager;
    }

    public List<InMemoryTeam> generateTeamsForSplit(InMemoryParty party) {
        List<UUID> shuffled = new ArrayList<>(party.getMemberList());
        Collections.shuffle(shuffled);

        int team1Size = (shuffled.size() + 1) / 2;
        List<UUID> team1 = new ArrayList<>(shuffled.subList(0, team1Size));
        List<UUID> team2 = new ArrayList<>(shuffled.subList(team1Size, shuffled.size()));

        return List.of(createTeam(team1), createTeam(team2));
    }

    private InMemoryTeam createTeam(List<UUID> members) {
        return new InMemoryTeam(
                members.stream()
                        .map(TeamPlayer::fromUuid)
                        .collect(Collectors.toCollection(ArrayList::new)),
                true);
    }

    public InMemoryParty getPartyByMember(UUID playerId) {
        return partyManager.getPartyByMember(playerId);
    }
}
