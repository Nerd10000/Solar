package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Player p = e.getPlayer();

        if (Solar.matchManager.isPlayerInAMatch(p.getUniqueId())) {

            InMemoryMatch match = Solar.matchManager.getMatchByMember(p.getUniqueId());

            if (match == null) return;

            InMemoryKit inMemoryKit = Solar.kitManager.getKit(match.getKit());

            if (inMemoryKit != null) {
                if (match.getStage() == MatchStageEnum.ONGOING
                        && inMemoryKit.flags.preventItemDrop()) {

                    e.setCancelled(true);
                }
            } else {

            }

            if (match.getStage() == MatchStageEnum.ENDED) {

                e.setCancelled(true);
            }
        }
    }
}
