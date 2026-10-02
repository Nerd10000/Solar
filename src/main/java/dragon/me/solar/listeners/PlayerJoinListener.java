package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.database.DatabaseManager;
import dragon.me.solar.database.models.PlayerStat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final ConfigManager configManager;
    private final DatabaseManager databaseManager;

    public PlayerJoinListener(ConfigManager configManager, DatabaseManager databaseManager) {
        this.configManager = configManager;
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        if (configManager.settingsRecord().lobbyRecord() == null) return;

        if (!configManager.settingsRecord().teleportToLobbyOnJoin()) return;

        p.teleport(new Location(
                Bukkit.getWorld(configManager.settingsRecord().lobbyRecord().name()),
                configManager.settingsRecord().lobbyRecord().x(),
                configManager.settingsRecord().lobbyRecord().y(),
                configManager.settingsRecord().lobbyRecord().z(),
                configManager.settingsRecord().lobbyRecord().yaw(),
                configManager.settingsRecord().lobbyRecord().pitch()));

        for (String kitName : Solar.kitManager.getKits().keySet()) {

            Solar.cache.getPlayer(p.getUniqueId(), kitName).thenAccept(stat -> {
                if (stat == null) {
                    stat = new PlayerStat();
                    stat.setUuid(p.getUniqueId().toString());
                    stat.setKit(kitName);
                    stat.setElo(configManager.settingsRecord().ratingSettings().defaultElo());

                    databaseManager.updateStats(stat);
                }
            });
        }

        Solar.databaseManager.getStoreById(e.getPlayer().getUniqueId()).thenAccept(store -> {
            if (store == null) return;

            //            p.getInventory().setContents((ItemStack[])
            // store.getContent());
            //            p.getInventory().setItemInOffHand((ItemStack)
            // store.getOffhand());
            //            p.getInventory().setArmorContents((ItemStack[])
            // store.getArmor());

            // TODO: Handle adding potion effects.

            p.teleportAsync(new Location(
                    Bukkit.getWorld(configManager.settingsRecord().lobbyRecord().name()),
                    configManager.settingsRecord().lobbyRecord().x(),
                    configManager.settingsRecord().lobbyRecord().y(),
                    configManager.settingsRecord().lobbyRecord().z(),
                    configManager.settingsRecord().lobbyRecord().yaw(),
                    configManager.settingsRecord().lobbyRecord().pitch()));

            databaseManager.removeStoreById(p.getUniqueId());
            Solar.instance.getLogger().info("Restored the state of " + p.getName() + " later as he left the match!");
        });
    }
}
