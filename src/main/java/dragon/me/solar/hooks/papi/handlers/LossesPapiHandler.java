package dragon.me.solar.hooks.papi.handlers;

import dragon.me.solar.Solar;
import dragon.me.solar.hooks.papi.PapiHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LossesPapiHandler implements PapiHandler {

    @Override
    public String identifier() {
        return "losses";
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
        String[] parts = params.split(":");

        if (parts.length < 1) {
            return null;
        }

        String kit = parts[0];

        OfflinePlayer target = player;

        if (parts.length >= 2 && !parts[1].isBlank()) {
            target = Bukkit.getOfflinePlayer(parts[1]);
        }

        if (kit.equals("*")) {
            return String.valueOf(getTotalLosses(target));
        }

        return getLosses(target, kit);
    }

    private int getTotalLosses(OfflinePlayer player) {
        int total = 0;

        for (String kit : Solar.kitManager.getKits().keySet()) {
            total += Integer.parseInt(getLosses(player, kit));
        }

        return total;
    }

    private String getLosses(OfflinePlayer player, String kit) {
        return String.valueOf(Solar.context().cache.getLosses(player.getUniqueId(), kit));
    }
}
