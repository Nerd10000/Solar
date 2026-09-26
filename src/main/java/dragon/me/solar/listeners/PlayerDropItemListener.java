package dragon.me.solar.listeners;

import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    private final MatchManager matchManager;
    private final KitManager kitManager;

    public PlayerDropItemListener(MatchManager matchManager, KitManager kitManager) {
        this.matchManager = matchManager;
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Player p = e.getPlayer();

        if (matchManager.isPlayerInAMatch(p.getUniqueId())) {

            InMemoryMatch match = matchManager.getMatchByMember(p.getUniqueId());

            if (match == null) return;

            InMemoryKit inMemoryKit = kitManager.getKit(match.getKit());

            if (inMemoryKit != null) {
                if (match.getStage() == MatchStageEnum.ONGOING && inMemoryKit.flags.preventItemDrop()) {

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
