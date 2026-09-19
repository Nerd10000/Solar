package dragon.me.solar.messages;

import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.configs.records.LanguageRecord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Arrays;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

public class MessageService {
    private final ConfigManager configManager;
    private final MiniMessage miniMessage;

    public MessageService(ConfigManager configManager, MiniMessage miniMessage) {
        this.configManager = configManager;
        this.miniMessage = miniMessage;
    }

    public Component render(String template, TagResolver... placeholders) {
        TagResolver prefix = Placeholder.parsed("prefix", configManager.languageRecord().prefix());
        TagResolver[] resolvers =
                Stream.concat(Stream.of(prefix), Arrays.stream(placeholders))
                        .toArray(TagResolver[]::new);
        return miniMessage.deserialize(template, resolvers);
    }

    public void send(Player player, String template, TagResolver... placeholders) {
        player.sendMessage(render(template, placeholders));
    }

    public void send(CommandSourceStack stack, String template, TagResolver... placeholders) {
        stack.getSender().sendMessage(render(template, placeholders));
    }

    public void sendConsoleError(CommandSourceStack stack) {
        send(stack, configManager.languageRecord().consoleCantRun());
    }

    public LanguageRecord language() {
        return configManager.languageRecord();
    }
}
