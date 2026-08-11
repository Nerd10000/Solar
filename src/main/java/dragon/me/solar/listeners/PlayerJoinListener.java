package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (Solar.configManager.settingsRecord().lobbyRecord() == null) return;

        if (!Solar.configManager.settingsRecord().teleportToLobbyOnJoin()) return;

        p.teleport(
                new Location(
                        Bukkit.getWorld(Solar.configManager.settingsRecord().lobbyRecord().name()),
                        Solar.configManager.settingsRecord().lobbyRecord().x(),
                        Solar.configManager.settingsRecord().lobbyRecord().y(),
                        Solar.configManager.settingsRecord().lobbyRecord().z(),
                        Solar.configManager.settingsRecord().lobbyRecord().yaw(),
                        Solar.configManager.settingsRecord().lobbyRecord().pitch()));
    }
}
