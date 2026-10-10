package dragon.me.solar.queue;

import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.utils.SoundUtils;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class QueueService {

    private final QueueManager queueManager;
    private final MatchService matchService;

    public QueueService(QueueManager queueManager, MatchService matchService) {

        this.queueManager = queueManager;
        this.matchService = matchService;
    }

    public boolean isAbleToStart(String queueName) {

        InMemoryQueue queue = queueManager.get(queueName);

        if (queue == null) {
            return false;
        }

        return queue.queue.size() >= (queue.flags.teamSize() * 2);
    }

    public void joinQueue(UUID playerId, String queueName) {

        InMemoryQueue queue = queueManager.get(queueName);

        if (queue == null) {
            return;
        }
        if (queue.queue.contains(playerId)) return;

        queue.join(playerId);
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {

            Solar.queueActionbarService.startActionbar(player, queue);
        }
        startMatch(Bukkit.getPlayer(playerId), queue);
    }

    public void leaveQueue(UUID playerId, String queueName) {

        InMemoryQueue queue = queueManager.get(queueName);

        if (queue == null) {
            return;
        }

        queue.leave(playerId);

        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {

            Solar.queueActionbarService.stopActionbar(player, queue);
        }
    }

    public void leaveAllQueues(UUID playerId) {

        for (Map.Entry<String, InMemoryQueue> entry : queueManager.queueManagerMap.entrySet()) {
            entry.getValue().leave(playerId);
            Solar.queueActionbarService.stopActionbar(Bukkit.getPlayer(playerId), entry.getValue());
        }
    }

    public void startMatch(Player p, InMemoryQueue queue) {

        if (queue != null) {

            if (!queue.isEnoughForNextMatch(queue.flags.teamSize())) {
                return;
            }
            List<UUID> players = Arrays.asList(queue.getNextParticipants(queue.flags.teamSize() * 2));

            List<List<UUID>> teams = new ArrayList<>();

            for (int i = 0; i < 2; i++) {
                int from = i * queue.flags.teamSize();
                int to = from + queue.flags.teamSize();

                teams.add(new ArrayList<>(players.subList(from, to)));
            }

            players.forEach(player -> {
                Player bukkitPlayer = Bukkit.getPlayer(player);

                if (bukkitPlayer != null) {
                    Solar.messageService.sendTitle(
                            bukkitPlayer,
                            Solar.messageService.language().matchFound(),
                            Solar.messageService.language().matchFoundSubtitle());

                    SoundUtils.playConfiguredSound(
                            bukkitPlayer,
                            Solar.configManager.settingsRecord().soundRecords().matchFound(),
                            Sound.ENTITY_PLAYER_LEVELUP);
                }
            });
            InMemoryMatch match = new InMemoryMatch(
                    teams.stream()
                            .map(team -> new InMemoryTeam(
                                    team.stream()
                                            .map(uuid -> new TeamPlayer(uuid, true))
                                            .collect(Collectors.toCollection(ArrayList::new)),
                                    true))
                            .collect(Collectors.toCollection(ArrayList::new)),
                    queue.kitId);

            int slot = Solar.gridManager.allocate();

            if (slot < 0) {
                Solar.instance.getLogger().warning("Failed to start a queue match: no available grid!");
                return;
            }

            match.setArenaName(resolveArenaName());
            match.setGridSlot(slot);

            matchService.startMatch(match, false, queue);

            match.setMatchSource(queue);

            for (TeamPlayer tp : match.getMembers()) {
                Player player = Bukkit.getPlayer(tp.uuid());

                if (player == null) continue;

                Solar.queueActionbarService.stopActionbar(player, queue);
            }
        }
    }

    private String resolveArenaName() {
        if (Solar.arenaManager.getArenas().isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current()
                .nextInt(Solar.arenaManager.getArenas().size());

        return Solar.arenaManager.getArenas().values().stream()
                .skip(index)
                .findFirst()
                .map(arena -> arena.name)
                .orElse(null);
    }
}
