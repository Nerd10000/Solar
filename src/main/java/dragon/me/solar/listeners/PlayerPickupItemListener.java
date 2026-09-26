package dragon.me.solar.listeners;

import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    private final MatchManager matchManager;

    public PlayerPickupItemListener(MatchManager matchManager) {
        this.matchManager = matchManager;
    }

    @EventHandler
    public void handleItemPickup(EntityPickupItemEvent e) {

        if (e.getEntity() instanceof Player p) {

            if (matchManager.isPlayerInAMatch(p.getUniqueId())) {

                InMemoryMatch match = matchManager.getMatchByMember(p.getUniqueId());

                for (InMemoryTeam team : match.getTeamList()) {

                    for (TeamPlayer tp : team.getMembers()) {

                        if (p.getUniqueId().equals(tp.uuid())) {
                            e.setCancelled(true);
                        }
                    }
                }

                if (match.getStage() == MatchStageEnum.ENDED) {

                    e.setCancelled(true);
                }
            }
        }
    }
}
