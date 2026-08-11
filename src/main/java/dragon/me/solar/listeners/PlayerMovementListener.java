package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.utils.MatchStageEnum;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMovementListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();

        InMemoryMatch match = Solar.matchManager.getMatchByMember(p.getUniqueId());

        if (match == null) return;

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(match.getKit());

        if (inMemoryKit == null) return;

        if (match.getStage() == MatchStageEnum.STARTING
                && inMemoryKit.flags.preventMovementBeforeStart()) {

            Location location = e.getFrom();
            location.setYaw(e.getTo().getYaw());
            location.setPitch(e.getTo().getPitch());

            p.teleport(location);
        }
    }
}
