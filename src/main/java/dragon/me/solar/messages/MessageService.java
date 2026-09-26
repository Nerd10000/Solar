package dragon.me.solar.messages;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.configs.records.LanguageRecord;
import dragon.me.solar.hooks.Compatibilities;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Arrays;
import java.util.stream.Stream;
import me.clip.placeholderapi.PlaceholderAPI;
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

    private Component deserialize(String template, TagResolver... placeholders) {
        TagResolver prefix =
                Placeholder.parsed("prefix", configManager.languageRecord().prefix());

        TagResolver[] resolvers =
                Stream.concat(Stream.of(prefix), Arrays.stream(placeholders)).toArray(TagResolver[]::new);

        return miniMessage.deserialize(template, resolvers);
    }

    public Component render(Player player, String template, TagResolver... placeholders) {
        if (player != null && Solar.compatibilityChecker.isCompatibleWith(Compatibilities.PAPI)) {
            template = PlaceholderAPI.setPlaceholders(player, template);
        }

        return deserialize(template, placeholders);
    }

    public Component render(CommandSourceStack stack, String template, TagResolver... placeholders) {
        return deserialize(template, placeholders);
    }

    public void send(Player player, String template, TagResolver... placeholders) {
        player.sendMessage(render(player, template, placeholders));
    }

    public void send(CommandSourceStack stack, String template, TagResolver... placeholders) {
        stack.getSender().sendMessage(render(stack, template, placeholders));
    }

    public void sendConsoleError(CommandSourceStack stack) {
        send(stack, configManager.languageRecord().consoleCantRun());
    }

    public LanguageRecord language() {
        return configManager.languageRecord();
    }
}
