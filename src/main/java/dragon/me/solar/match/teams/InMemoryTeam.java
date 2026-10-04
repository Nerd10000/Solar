package dragon.me.solar.match.teams;

import dragon.me.solar.Solar;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.queue.InMemoryQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class InMemoryTeam {

    private List<TeamPlayer> members = new ArrayList<>();

    private boolean isTeamAlive = true;
    private int roundWins = 0;

    public InMemoryTeam(List<TeamPlayer> members, boolean isTeamAlive) {
        this.members = members;
        this.isTeamAlive = isTeamAlive;
    }

    public List<TeamPlayer> getMembers() {
        return members;
    }

    public boolean isTeamAlive() {
        return isTeamAlive;
    }

    public void setTeamAlive(boolean teamAlive) {
        isTeamAlive = teamAlive;
    }

    public void setMemberStateTo(boolean isAlive, UUID uuid) {
        for (int i = 0; i < members.size(); i++) {
            TeamPlayer player = members.get(i);

            if (player.uuid().equals(uuid)) {
                members.set(i, player.updateStatus(isAlive));
                return;
            }
        }
    }

    public int getRoundWins() {
        return roundWins;
    }

    public void setRoundWins(int roundWins) {
        this.roundWins = roundWins;
    }

    public int getAvgRating(InMemoryQueue queue) {

        if (queue.flags.weight().equalsIgnoreCase("None")) return 0;

        int sum = 0;

        for (TeamPlayer member : members) {
            sum += Solar.cache.getElo(member.uuid(), queue.kitId) == 0
                    ? Solar.configManager.settingsRecord().ratingSettings().defaultElo()
                    : Solar.cache.getElo(member.uuid(), queue.kitId);
        }

        return sum / members.size();
    }

    public void updateTeamStatus() {

        int deadCount = 0;
        for (TeamPlayer player : members) {

            if (!player.isAlive()) deadCount++;
        }

        if (deadCount == members.size()) {

            isTeamAlive = false;

        } else {
            isTeamAlive = true;
        }
    }

    public void broadcastMessage(Component component) {

        for (TeamPlayer player : members) {

            Bukkit.getPlayer(player.uuid()).sendMessage(component);
        }
    }
}
