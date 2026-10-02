package dragon.me.solar.commands.args.queues;

import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.kit.KitService;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.queue.QueueManager;
import dragon.me.solar.queue.QueueService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public record QueueCommandContext(
        QueueManager queueManager,
        KitService kitService,
        ConfigManager configManager,
        MessageService messages,
        QueueService queueService) {

    public void send(Player player, String template, TagResolver... placeholders) {
        messages.send(player, template, placeholders);
    }

    public void send(CommandSourceStack stack, String template, TagResolver... placeholders) {
        messages.send(stack, template, placeholders);
    }

    public void actionBar(Player player, String template, TagResolver... placeholders) {
        messages.sendActionBar(player, template, placeholders);
    }

    public void sendConsoleError(CommandSourceStack stack) {
        messages.sendConsoleError(stack);
    }
}
