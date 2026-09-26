package dragon.me.solar.hooks.papi;

import dragon.me.solar.Solar;
import java.util.Locale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarExpansion extends PlaceholderExpansion {

    private final Solar instance;

    public SolarExpansion(Solar instance) {
        this.instance = instance;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "solar";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DragonPvp";
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        String[] parts = params.split(":", 3);

        if (parts.length < 2) {
            return null;
        }

        String category = parts[0].toLowerCase(Locale.ROOT);
        String kit = parts[1];

        return switch (category) {
            case "wins" -> resolveWins(player, kit, parts);
            case "losses" -> resolveLosses(player, kit, parts);
            case "wlr" -> resolveWlr(player, kit, parts);
            case "winrate" -> resolveWinrate(player, kit, parts);
            default -> null;
        };
    }

    private String resolveWlr(OfflinePlayer player, String kit, String[] parts) {
        OfflinePlayer target = resolveTarget(player, parts);

        if (target == null) {
            return null;
        }

        int wins = kit.equals("*") ? getTotalWins(target) : Integer.parseInt(getWins(target, kit));

        int losses = kit.equals("*") ? getTotalLosses(target) : Integer.parseInt(getLosses(target, kit));

        if (losses == 0) {
            return wins == 0 ? "0.00" : "∞";
        }

        return String.format("%.2f", (double) wins / losses);
    }

    private String resolveWinrate(OfflinePlayer player, String kit, String[] parts) {
        OfflinePlayer target = resolveTarget(player, parts);

        if (target == null) {
            return null;
        }

        int wins = kit.equals("*") ? getTotalWins(target) : Integer.parseInt(getWins(target, kit));

        int losses = kit.equals("*") ? getTotalLosses(target) : Integer.parseInt(getLosses(target, kit));

        int games = wins + losses;

        if (games == 0) {
            return "0.00%";
        }

        return String.format("%.2f%%", (double) wins / games * 100);
    }

    private String resolveWins(OfflinePlayer player, String kit, String[] parts) {
        OfflinePlayer target = resolveTarget(player, parts);

        if (target == null) {
            return null;
        }

        if (kit.equals("*")) {
            return String.valueOf(getTotalWins(target));
        }

        return getWins(target, kit);
    }

    private String resolveLosses(OfflinePlayer player, String kit, String[] parts) {
        OfflinePlayer target = resolveTarget(player, parts);

        if (target == null) {
            return null;
        }

        if (kit.equals("*")) {
            return String.valueOf(getTotalLosses(target));
        }

        return getLosses(target, kit);
    }

    private OfflinePlayer resolveTarget(OfflinePlayer defaultPlayer, String[] parts) {
        if (parts.length < 3 || parts[2].isBlank()) {
            return defaultPlayer;
        }

        return Bukkit.getOfflinePlayer(parts[2]);
    }

    private int getTotalWins(OfflinePlayer player) {
        int total = 0;

        for (String kit : Solar.kitManager.getKits().keySet()) {
            total += Integer.parseInt(getWins(player, kit));
        }

        return total;
    }

    private int getTotalLosses(OfflinePlayer player) {
        int total = 0;

        for (String kit : Solar.kitManager.getKits().keySet()) {
            total += Integer.parseInt(getLosses(player, kit));
        }

        return total;
    }

    private String getWins(OfflinePlayer player, String kit) {
        return String.valueOf(Solar.context().cache.getWins(player.getUniqueId(), kit));
    }

    private String getLosses(OfflinePlayer player, String kit) {
        return String.valueOf(Solar.context().cache.getLosses(player.getUniqueId(), kit));
    }
}
