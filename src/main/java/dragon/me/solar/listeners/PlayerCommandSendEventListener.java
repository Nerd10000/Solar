package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.messages.MessageService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandSendEventListener implements Listener {

    private final ConfigManager configManager;
    private final MessageService messageService;

    public PlayerCommandSendEventListener(ConfigManager configManager, MessageService messageService) {
        this.configManager = configManager;
        this.messageService = messageService;
    }

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandPreprocessEvent event) {

        if (!Solar.matchManager.isPlayerInAMatch(event.getPlayer().getUniqueId())) return;

        for (String s : configManager.settingsRecord().whitelistedMatchCommands()) {

            Pattern pattern = Pattern.compile(s);

            Matcher matcher = pattern.matcher(event.getMessage());

            if (matcher.matches()) {

                // TODO Add message here!
                event.setCancelled(true);
            }
        }
    }
}
