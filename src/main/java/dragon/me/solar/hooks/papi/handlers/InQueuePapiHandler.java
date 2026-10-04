package dragon.me.solar.hooks.papi.handlers;

import dragon.me.solar.Solar;
import dragon.me.solar.hooks.papi.PapiHandler;
import dragon.me.solar.queue.InMemoryQueue;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class InQueuePapiHandler implements PapiHandler {
    @Override
    public String identifier() {
        return "inqueue";
    }

    @Override
    public String handleOffline(OfflinePlayer player, String params) {

        String[] parts = params.isBlank() ? new String[0] : params.split(":");

        String queue = parts.length >= 1 && !parts[0].isBlank() ? parts[0] : "*";

        return handle(player, queue);
    }

    @Override
    public String handlePlayer(Player player, String params) {

        String[] parts = params.isBlank() ? new String[0] : params.split(":");

        String queue = parts.length >= 1 && !parts[0].isBlank() ? parts[0] : "*";

        return handle(player, queue);
    }

    private String handle(OfflinePlayer player, String queue) {

        InMemoryQueue inMemoryQueue = Solar.queueManager.get(queue);

        if (inMemoryQueue == null) {
            return "";
        }

        return String.valueOf(inMemoryQueue.queue.size());
    }
}
