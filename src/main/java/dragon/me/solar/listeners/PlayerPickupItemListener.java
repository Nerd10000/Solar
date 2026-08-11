package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void handleItemPickup(EntityPickupItemEvent e) {

        if (e.getEntity() instanceof Player p) {

            if (Solar.matchManager.isPlayerInAMatch(p.getUniqueId())) {

                InMemoryMatch match = Solar.matchManager.getMatchByMember(p.getUniqueId());

                if (match.getStage() == MatchStageEnum.ENDED) {

                    e.setCancelled(true);
                }
            }
        }
    }
}
