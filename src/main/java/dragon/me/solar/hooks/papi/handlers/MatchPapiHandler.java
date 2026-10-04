package dragon.me.solar.hooks.papi.handlers;

import dragon.me.solar.Solar;
import dragon.me.solar.hooks.papi.PapiHandler;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MatchPapiHandler implements PapiHandler {

    @Override
    public String identifier() {
        return "match";
    }

    @Override
    public String handleOffline(OfflinePlayer player, String params) {
        return "false";
    }

    @Override
    public String handlePlayer(Player player, String params) {
        if (params == null || params.isBlank()) {
            return null;
        }

        String[] parts = params.split(":");
        String type = parts[0];

        if (type.isBlank()) {
            return null;
        }

        return switch (type) {
            case "active" -> handleActive(player);
            case "kit" -> handleKit(player);
            case "arena" -> handleArena(player);
            case "state" -> handleState(player);
            case "opponents" -> handleOpponents(player, parts);
            case "duration" -> handleDuration(player);
            default -> null;
        };
    }

    private InMemoryMatch getMatch(Player player) {
        return Solar.matchManager.getMatchByMember(player.getUniqueId());
    }

    private String handleActive(Player player) {
        return String.valueOf(getMatch(player) != null);
    }

    private String handleKit(Player player) {
        InMemoryMatch match = getMatch(player);

        if (match == null) {
            return "null";
        }

        return match.getKit();
    }

    private String handleArena(Player player) {
        InMemoryMatch match = getMatch(player);

        if (match == null) {
            return "null";
        }

        return match.getArenaName();
    }

    private String handleState(Player player) {
        InMemoryMatch match = getMatch(player);

        if (match == null) {
            return "null";
        }

        return match.getStage().name();
    }

    private String handleOpponents(Player player, String[] parts) {
        InMemoryMatch match = getMatch(player);

        if (match == null) {
            return "null";
        }

        int count = 2;

        if (parts.length >= 2 && !parts[1].isBlank()) {
            try {
                count = Math.max(1, Integer.parseInt(parts[1]));
            } catch (NumberFormatException ignored) {
                // Use default.
            }
        }

        InMemoryTeam ownTeam = match.getTeamByMember(player.getUniqueId());

        if (ownTeam == null) {
            return "null";
        }

        List<String> names = new ArrayList<>();
        boolean hasMore = false;

        for (InMemoryTeam team : match.getTeamList()) {
            if (team.equals(ownTeam)) {
                continue;
            }

            for (TeamPlayer teamPlayer : team.getMembers()) {
                if (names.size() >= count) {
                    hasMore = true;
                    break;
                }

                Player target = Bukkit.getPlayer(teamPlayer.uuid());

                if (target != null) {
                    names.add(target.getName());
                }
            }

            if (hasMore) {
                break;
            }
        }

        if (names.isEmpty()) {
            return "";
        }

        String result = String.join(", ", names);

        return hasMore ? result + " ..." : result;
    }

    private String handleDuration(Player player) {
        InMemoryMatch match = getMatch(player);

        if (match == null) {
            return "null";
        }

        Instant start = Instant.ofEpochMilli(match.getStartMillis());
        Duration duration = Duration.between(start, Instant.now());

        long seconds = Math.max(0, duration.getSeconds());

        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
