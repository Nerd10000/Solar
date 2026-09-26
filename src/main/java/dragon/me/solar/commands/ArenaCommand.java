package dragon.me.solar.commands;

import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.configs.ConfigManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.suggestion.Suggestions;

/** Compatibility type that provides shared arena argument suggestions. */
public class ArenaCommand {
    private final ArenaManager arenaManager;
    private ConfigManager configManager;
    private MiniMessage miniMessage;

    public ArenaCommand(ArenaManager arenaManager, ConfigManager configManager, MiniMessage miniMessage) {
        this.arenaManager = arenaManager;
        this.configManager = configManager;
        this.miniMessage = miniMessage;
    }

    @Command("arenas")
    public void noArgPartyCommand(CommandSourceStack stack) {

        List<String> unparsed = configManager.languageRecord().arenasHelp();

        for (String s : unparsed) {

            stack.getSender()
                    .sendMessage(miniMessage.deserialize(
                            s,
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix())));
        }
    }

    @Command("arenas help")
    public void helpCommand(CommandSourceStack stack) {

        List<String> unparsed = configManager.languageRecord().arenasHelp();

        for (String s : unparsed) {

            stack.getSender()
                    .sendMessage(miniMessage.deserialize(
                            s,
                            Placeholder.parsed(
                                    "prefix", configManager.languageRecord().prefix())));
        }
    }

    @Suggestions("arenas")
    public List<String> arenaSuggestions() {
        return arenaManager.getArenas().keySet().stream().toList();
    }
}
