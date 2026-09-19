package dragon.me.solar.listeners;

import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemUseListener implements Listener {

    private final MatchManager matchManager;
    private final KitManager kitManager;

    public PlayerItemUseListener(MatchManager matchManager, KitManager kitManager) {
        this.matchManager = matchManager;
        this.kitManager = kitManager;
    }

    @EventHandler
    public void onHandleItemUse(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        if (matchManager.isPlayerInAMatch(p.getUniqueId())) {

            InMemoryMatch match = matchManager.getMatchByMember(p.getUniqueId());

            if (match == null) return;

            InMemoryKit inMemoryKit = kitManager.getKit(match.getKit());

            if (e.getAction().isRightClick() && match.getStage() == MatchStageEnum.ENDED) {

                e.setCancelled(true);
            }

            if (inMemoryKit != null && e.getItem() != null) {

                Material type = e.getItem().getType();
                if (type == Material.ENDER_PEARL) {

                    if (p.hasCooldown(type)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (inMemoryKit.flags.pearlCooldown() == -1) {

                    } else {
                        p.setCooldown(type, 20 * inMemoryKit.flags.pearlCooldown());
                    }
                } else if (type == Material.WIND_CHARGE) {
                    if (p.hasCooldown(type)) {
                        e.setCancelled(true);
                        return;
                    }
                    if (inMemoryKit.flags.windChargeCooldown() == -1) {

                    } else {
                        p.setCooldown(type, 20 * inMemoryKit.flags.windChargeCooldown());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {

        InMemoryMatch match = matchManager.getMatchByMember(e.getPlayer().getUniqueId());

        if (match == null) return;

        InMemoryKit inMemoryKit = kitManager.getKit(match.getKit());

        if (inMemoryKit == null) return;

        Player p = e.getPlayer();
        Material type = e.getItem().getType();

        if (inMemoryKit.flags.goldenAppleCooldown() == -1) return;

        if (type != Material.GOLDEN_APPLE && type != Material.ENCHANTED_GOLDEN_APPLE) {
            return;
        }
        if (p.hasCooldown(type)) {
            e.setCancelled(true);
            return;
        }
        p.setCooldown(type, 20 * inMemoryKit.flags.goldenAppleCooldown());
    }
}
