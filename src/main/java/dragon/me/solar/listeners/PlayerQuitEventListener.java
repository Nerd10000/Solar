package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!Solar.matchManager.isPlayerInAMatch(uuid)) {
            return;
        }

        InMemoryMatch match = Solar.matchManager.getMatchByMember(uuid);
        if (match == null) {
            return;
        }

        InMemoryTeam winner =
                match.getTeamList().stream()
                        .filter(
                                team ->
                                        team.getMembers().stream()
                                                .noneMatch(member -> member.uuid().equals(uuid)))
                        .findFirst()
                        .orElse(null);

        Solar.matchManager.endMatch(match, winner, MatchEndReason.FORFEIT);
    }
}
