package dragon.me.solar.hooks.papi;

import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.party.InMemoryParty;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
        String[] args = params.split(":");

        if (args.length == 0) {
            return null;
        }
        instance.getLogger().info("[PAPI-DEBUG] params=" + Arrays.toString(args));
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "wins":
                if (args.length < 3) {
                    return null;
                }

                return handleWins(
                        player.getPlayer(),
                        args[1],
                        Bukkit.getOfflinePlayer(args[2].replace("{player}", player.getName())));
            case "losses":
                if (args.length < 3) {
                    return null;
                }

                return handleLosses(
                        player.getPlayer(),
                        args[1],
                        Bukkit.getOfflinePlayer(args[2].replace("{player}", player.getName())));
            case "elo":
                if (args.length < 3) {
                    return null;
                }
                return handleElo(
                        player.getPlayer(),
                        args[1],
                        Bukkit.getOfflinePlayer(args[2].replace("{player}", player.getName())));
            case "inqueue":
                if (args.length < 2) {
                    return null;
                }

                return handleInQueue(player.getPlayer(), args[1]);
            case "isinqueue":
                if (args.length < 3) {
                    return null;
                }

                return handleIsInQueue(
                        player.getPlayer(),
                        args[1],
                        Bukkit.getOfflinePlayer(args[2].replace("{player}", player.getName())));
            case "winrate":
                if (args.length < 3) {
                    return null;
                }

                return handleWinrate(
                        player.getPlayer(),
                        args[1],
                        Bukkit.getOfflinePlayer(args[2].replace("{player}", player.getName())));

            case "match":
                if (args.length < 2) {
                    return null;
                }

                return handleMatchPlaceholders(player.getPlayer(), args[1], args);
            case "party":
                if (args.length < 2) {
                    return null;
                }

                return handleParty(player.getPlayer(), args);

            default:
                return null;
        }
    }

    // Party placeholders
    private String handleParty(Player player, String[] params) {

        UUID uuid = player.getUniqueId();

        InMemoryParty party = Solar.context().partyManager.getPartyByMember(uuid);
        int count = params.length == 3 ? Integer.parseInt(params[2]) : 1;
        switch (params[1].toLowerCase(Locale.ROOT)) {
            case "inparty":
                return String.valueOf(party != null);
            case "leader":
                if (party == null) return "null";
                return Bukkit.getPlayer(party.getOwner()).getName();
            case "size":
                if (party == null) return "null";
                return String.valueOf(party.getMemberList().size());
            case "max":
                if (party == null) return "null";
                return String.valueOf(party.getMaxMembers());
            case "members":
                if (party == null) return "null";
                List<String> names = new ArrayList<>();
                int c = 0;
                for (UUID u : party.getMemberList()) {
                    if (c >= count) break;

                    names.add(u.toString());
                }
                boolean hasMore = names.size() > count;

                return String.join(", ", names) + (hasMore ? "..." : "");
        }

        return "null";
    }

    //  Match placeholders ( %solar_match:xxxx% )
    private String handleMatchPlaceholders(Player player, String arg, String[] params) {

        UUID uuid = player.getUniqueId();

        InMemoryMatch match = Solar.context().matchManager.getMatchByMember(uuid);

        int count = params.length == 3 ? Integer.parseInt(params[2]) : 1;

        switch (arg.toLowerCase(Locale.ROOT)) {
            case "active":
                return String.valueOf(match != null);

            case "kit":
                if (match != null) return match.getKit();
                return "null";

            case "arena":
                if (match != null) return match.getArenaName();
                return "null";

            case "state":
                if (match != null) return match.getStage().name();
                return "null";
            case "rounds":
                if (match != null) return String.valueOf("FT" + match.getRounds());
                return "null";

            case "round":
                if (match != null) return String.valueOf(match.getCurrentRound());
                return "null";

            case "duration":
                if (match != null) {

                    Instant start = Instant.ofEpochMilli(match.getStartMillis());
                    Duration duration = Duration.between(start, Instant.now());

                    long secs = Math.max(0, duration.getSeconds());
                    long mins = secs / 60;
                    long remainingSecs = secs % 60;

                    return String.format("%02d:%02d", mins, remainingSecs);
                }

                return "null";
            case "opponents":
                if (match != null) {

                    List<String> names = new ArrayList<>();

                    InMemoryTeam ownTeam = match.getTeamByMember(uuid);

                    if (ownTeam == null) {
                        return "null";
                    }

                    for (InMemoryTeam team : match.getTeamList()) {

                        if (team.equals(ownTeam)) {
                            continue;
                        }

                        for (TeamPlayer tp : team.getMembers()) {

                            Player target = Bukkit.getPlayer(tp.uuid());

                            if (target == null) {
                                continue;
                            }

                            names.add(target.getName());

                            if (names.size() > count) {
                                break;
                            }
                        }

                        if (names.size() > count) {
                            break;
                        }
                    }

                    if (names.isEmpty()) {
                        return "";
                    }

                    boolean hasMore = names.size() > count;

                    if (hasMore) {
                        names = names.subList(0, count);
                    }

                    return String.join(", ", names) + (hasMore ? "..." : "");
                }

                return "null";
        }

        return null;
    }

    // Is in queue placeholder
    private String handleIsInQueue(Player player, String queue, OfflinePlayer target) {

        UUID uuid = target != null ? target.getUniqueId() : player.getUniqueId();

        // All queues
        if ("*".equals(queue)) {

            for (var queueManager : Solar.queueManager.queueManagerMap.values()) {
                if (queueManager.queue.contains(uuid)) {
                    return "true";
                }
            }

            return "false";
        }

        // Specific queue
        if (queue != null && !queue.isBlank()) {

            var queueManager = Solar.queueManager.queueManagerMap.get(queue);

            if (queueManager == null) {
                return "false";
            }

            return String.valueOf(queueManager.queue.contains(uuid));
        }

        return "false";
    }

    // inq. placeholder
    private String handleInQueue(Player player, String queue) {

        // UUID uuid = player.getUniqueId();

        // All queues
        if ("*".equals(queue)) {
            int sum = 0;

            for (String s : Solar.queueManager.queueManagerMap.keySet()) {

                sum += Solar.queueManager.get(s).queue.size();
            }

            return String.valueOf(sum);
        }

        // Specific kit
        if (queue != null && !queue.isBlank()) {

            int queueSize = Solar.queueManager.get(queue).queue.size();

            return String.valueOf(queueSize);
        }

        return null;
    }

    // Win placeholder
    private String handleWins(Player player, String kit, OfflinePlayer target) {

        UUID uuid = target != null ? target.getUniqueId() : player.getUniqueId();

        // All kits
        if ("*".equals(kit)) {
            int sum = 0;

            for (String kitId : Solar.context().kitManager.getKits().keySet()) {
                sum += Solar.context().cache.getWins(uuid, kitId);
            }

            return String.valueOf(sum);
        }

        // Specific kit
        if (kit != null && !kit.isBlank()) {

            return String.valueOf(Solar.context().cache.getWins(uuid, kit));
        }

        return null;
    }

    // Losses placeholder
    private String handleLosses(Player player, String kit, OfflinePlayer target) {

        UUID uuid = target != null ? target.getUniqueId() : player.getUniqueId();

        // All kits
        if ("*".equals(kit)) {
            int sum = 0;

            for (String kitId : Solar.context().kitManager.getKits().keySet()) {
                sum += Solar.context().cache.getLosses(uuid, kitId);
            }
            instance.getLogger().info("[PAPI-DEBUG] kit=" + kit + ", target=" + target.getName() + ", sum=" + sum);
            return String.valueOf(sum);
        }

        // Specific kit
        if (kit != null && !kit.isBlank()) {
            instance.getLogger()
                    .info("[PAPI-DEBUG] kit=" + kit + ", target=" + target.getName() + ", wins="
                            + Solar.context().cache.getWins(uuid, kit));
            return String.valueOf(Solar.context().cache.getLosses(uuid, kit));
        }
        instance.getLogger().info("[PAPI-DEBUG] kit=" + kit + ", target=" + target.getName());
        return null;
    }

    // Elo placeholder
    private String handleElo(Player player, String queue, OfflinePlayer target) {

        UUID uuid = target != null ? target.getUniqueId() : player.getUniqueId();

        // All kits
        if ("*".equals(queue)) {
            int sum = 0;

            for (String queueId : Solar.queueManager.queueManagerMap.keySet()) {
                sum += Solar.context().cache.getElo(uuid, queueId);
            }

            return String.valueOf(sum / Solar.queueManager.queueManagerMap.size());
        }

        // Specific kit
        if (queue != null && !queue.isBlank()) {
            return String.valueOf(Solar.context().cache.getElo(uuid, queue));
        }

        return null;
    }

    // Winrate placeholder
    private String handleWinrate(Player player, String queue, OfflinePlayer target) {

        UUID uuid = target != null ? target.getUniqueId() : player.getUniqueId();

        if ("*".equals(queue)) {
            int wins = 0;
            int losses = 0;

            for (String queueId : Solar.queueManager.queueManagerMap.keySet()) {
                wins += Solar.context().cache.getWins(uuid, queueId);
                losses += Solar.context().cache.getLosses(uuid, queueId);
            }

            int games = wins + losses;

            if (games == 0) {
                return "0.0%";
            }

            return String.format("%.2f%%", (double) wins / games * 100);
        }

        if (queue != null && !queue.isBlank()) {
            int wins = Solar.context().cache.getWins(uuid, queue);
            int losses = Solar.context().cache.getLosses(uuid, queue);

            int games = wins + losses;

            if (games == 0) {
                return "0.0%";
            }

            return String.format("%.2f%%", (double) wins / games * 100);
        }

        return null;
    }
}
