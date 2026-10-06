package dragon.me.solar.hooks.papi.handlers;

import dragon.me.solar.Solar;
import dragon.me.solar.hooks.papi.PapiHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class WinratePapiHandler implements PapiHandler {

    @Override
    public String identifier() {
        return "winrate";
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

        if (parts.length == 0 || parts[0].isBlank()) {
            return null;
        }

        String kit = parts[0];

        OfflinePlayer target = player;

        if (parts.length >= 2 && !parts[1].isBlank()) {
            target = Bukkit.getOfflinePlayer(parts[1]);
        }

        return resolveWinrate(target, kit);
    }

    private String resolveWinrate(OfflinePlayer player, String kit) {

        int wins = kit.equals("*") ? getTotalWins(player) : getWins(player, kit);

        int losses = kit.equals("*") ? getTotalLosses(player) : getLosses(player, kit);

        int games = wins + losses;

        if (games == 0) {
            return "0.00%";
        }

        return String.format("%.2f%%", (double) wins / games * 100);
    }

    private int getWins(OfflinePlayer player, String kit) {
        return Solar.context().cache.getWins(player.getUniqueId(), kit);
    }

    private int getLosses(OfflinePlayer player, String kit) {
        return Solar.context().cache.getLosses(player.getUniqueId(), kit);
    }

    private int getTotalWins(OfflinePlayer player) {
        int total = 0;

        for (String kit : Solar.kitManager.getKits().keySet()) {
            total += getWins(player, kit);
        }

        return total;
    }

    private int getTotalLosses(OfflinePlayer player) {
        int total = 0;

        for (String kit : Solar.kitManager.getKits().keySet()) {
            total += getLosses(player, kit);
        }

        return total;
    }
}
