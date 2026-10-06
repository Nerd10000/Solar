package dragon.me.solar.match;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.match.player.PlayerSnapshot;
import dragon.me.solar.match.player.TeamPlayer;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MatchPlayerStateService {
    private final Solar plugin;
    private final ConfigManager configManager;

    public MatchPlayerStateService(Solar plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void saveSnapshots(InMemoryMatch match) {
        for (TeamPlayer teamPlayer : match.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player != null) {
                match.saveInventory(player);
            }
        }
    }

    public void clearInventories(InMemoryMatch match) {
        forEachOnlineMember(match, player -> player.getInventory().clear());
    }

    public void restoreAndTeleport(InMemoryMatch match) {
        Location lobby = configManager.getLobbyLocation();
        for (TeamPlayer teamPlayer : match.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player == null) {
                continue;
            }
            if (player.isDead()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.spigot().respawn(), 1L);
            }

            PlayerSnapshot snapshot = match.getSavedInventories().get(teamPlayer.uuid());
            if (snapshot != null) {
                snapshot.restore(player);
            }
            if (lobby != null) {
                Bukkit.getScheduler()
                        .runTaskLater(
                                plugin,
                                () -> {
                                    if (player.isOnline()) {
                                        player.teleport(lobby);
                                        player.setInvulnerable(false);
                                        player.setAllowFlight(false);
                                        player.setGameMode(GameMode.SURVIVAL);
                                        player.setFireTicks(0);
                                        player.heal(Integer.MAX_VALUE);
                                        player.setFoodLevel(20);
                                    }
                                },
                                5 * 20L);
            }
        }
    }

    public void resetSpectators(InMemoryMatch match) {
        for (UUID uuid : match.getSpectatorList()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(configManager.getLobbyLocation());
            }
        }
    }

    public void resetMaxHealth(InMemoryMatch match) {
        forEachOnlineMember(match, player -> player.setMaxHealth(20));
    }

    private void forEachOnlineMember(InMemoryMatch match, Consumer<Player> action) {
        for (TeamPlayer teamPlayer : match.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player != null) {
                action.accept(player);
            }
        }
    }
}
