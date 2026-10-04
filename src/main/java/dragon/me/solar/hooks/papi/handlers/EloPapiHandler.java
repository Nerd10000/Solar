package dragon.me.solar.hooks.papi.handlers;

import dragon.me.solar.Solar;
import dragon.me.solar.hooks.papi.PapiHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class EloPapiHandler implements PapiHandler {

    @Override
    public String identifier() {
        return "elo";
    }

    @Override
    public String handleOffline(OfflinePlayer player, String params) {
        return handle(player, params);
    }

    @Override
    public String handlePlayer(Player player, String params) {
        return handle(player, params);
    }

    private String handle(OfflinePlayer player, String params) {

        String[] parts = params.isBlank() ? new String[0] : params.split(":");

        String queue = parts.length >= 1 && !parts[0].isBlank() ? parts[0] : "*";

        OfflinePlayer target = player;

        if (parts.length >= 2 && !parts[1].isBlank()) {
            target = Bukkit.getOfflinePlayer(parts[1]);
        }

        return String.valueOf(getElo(target, queue));
    }

    private int getElo(OfflinePlayer player, String queue) {

        if (queue.equals("*")) {

            int sum = 0;
            int count = 0;

            for (String key : Solar.queueManager.queueManagerMap.keySet()) {
                sum += Solar.cache.getElo(player.getUniqueId(), key);
                count++;
            }

            return count == 0 ? 0 : sum / count;
        }

        return Solar.cache.getElo(player.getUniqueId(), queue);
    }
}
