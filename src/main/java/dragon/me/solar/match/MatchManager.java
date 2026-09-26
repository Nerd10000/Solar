package dragon.me.solar.match;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MatchManager {

    private final List<InMemoryMatch> matchList = new ArrayList<>();

    public MatchManager() {}

    public List<InMemoryMatch> getMatchList() {
        return List.copyOf(matchList);
    }

    public void remove(UUID uuid) {
        matchList.removeIf(match -> match.getUuid().equals(uuid));
    }

    public void add(InMemoryMatch match) {
        matchList.add(match);
    }

    public @Nullable InMemoryMatch getMatchByMember(UUID uuid) {

        for (InMemoryMatch match : matchList) {
            if (match.getMembers().stream().anyMatch(member -> member.uuid().equals(uuid))) {
                return match;
            }
        }
        return null;
    }

    public boolean isPlayerInAMatch(UUID playerId) {

        for (InMemoryMatch match : matchList) {

            if (match.getMembers().stream().anyMatch(m -> m.uuid().equals(playerId))) {
                return true;
            }
        }
        return false;
    }

    public boolean isSpectatingAlready(UUID playerId) {
        return matchList.stream().anyMatch(match -> match.getSpectatorList().contains(playerId));
    }
}
