package dragon.me.solar.match.teams;

import dragon.me.solar.match.player.TeamPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class InMemoryTeam {

    private List<TeamPlayer> members = new ArrayList<>();

    private boolean isTeamAlive = true;

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

        for (TeamPlayer player : members) {

            if (player.uuid().equals(uuid)) {

                members.remove(player);
                members.add(player.updateStatus(isAlive));
            }
        }
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
