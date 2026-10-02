package dragon.me.solar.queue;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.messages.MessageService;
import java.time.Duration;
import java.util.*;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueueActionbarService {

    private final QueueService queueService;
    private final ConfigManager configManager;
    private final MessageService messageService;

    // Signature: 1. UUID, 2. Queue name. <- key Long <- time
    private final HashMap<QueueActionbarKey, Long> container = new HashMap<>();

    public QueueActionbarService(
            QueueService queueService, ConfigManager configManager, MessageService messageService) {
        this.queueService = queueService;
        this.configManager = configManager;
        this.messageService = messageService;
    }

    public void startActionbar(Player player, InMemoryQueue inMemoryQueue) {
        container.put(new QueueActionbarKey(player.getUniqueId(), inMemoryQueue.name), System.currentTimeMillis());
    }

    public void stopActionbar(Player player, InMemoryQueue inMemoryQueue) {
        container.remove(new QueueActionbarKey(player.getUniqueId(), inMemoryQueue.name));
    }

    public void tick() {

        Bukkit.getScheduler()
                .runTaskTimer(
                        Solar.instance,
                        task -> {
                            for (Map.Entry<QueueActionbarKey, Long> entry : container.entrySet()) {

                                UUID playerId = (UUID) entry.getKey().uuid();
                                String queueName = (String) entry.getKey().queueName();

                                long joinTime = entry.getValue();

                                Player player = Bukkit.getPlayer(playerId);

                                if (player == null) continue;

                                long elapsedTime = System.currentTimeMillis() - joinTime;

                                Duration duration = Duration.ofMillis(elapsedTime);

                                long minutes = duration.toMinutes();
                                long seconds = duration.minusMinutes(minutes).toSeconds();

                                String formatted = String.format(
                                        "%dm %ds", minutes, seconds); // TODO: Make the format configurable.

                                List<String> whereInQueue = container.entrySet().stream()
                                        .filter(queue -> queue.getKey().uuid().equals(player.getUniqueId()))
                                        .map(queue -> queue.getKey().queueName())
                                        .toList();

                                messageService.sendActionBar(
                                        player,
                                        messageService.language().inQueueActionbar(),
                                        Placeholder.parsed("time", formatted),
                                        Placeholder.parsed("queues", String.join(", ", whereInQueue)));
                            }
                        },
                        0,
                        20L);
    }
}
