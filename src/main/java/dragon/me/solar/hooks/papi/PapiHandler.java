package dragon.me.solar.hooks.papi;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PapiHandler {

    String identifier();

    String handleOffline(OfflinePlayer player, String params);

    String handlePlayer(Player player, String params);
}
