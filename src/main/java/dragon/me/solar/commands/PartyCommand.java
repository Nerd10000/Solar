package dragon.me.solar.commands;

import dragon.me.solar.configs.ConfigManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.annotations.Command;

/** Compatibility type for integrations that referenced the former aggregate party command. */
public class PartyCommand {

    private ConfigManager configManager;
    private MiniMessage miniMessage;

    public PartyCommand(ConfigManager configManager, MiniMessage miniMessage) {

        this.configManager = configManager;
        this.miniMessage = miniMessage;
    }

    @Command("party")
    public void noArgPartyCommand(CommandSourceStack stack) {

        List<String> unparsed = configManager.languageRecord().partyHelp();

        for (String s : unparsed) {

            stack.getSender()
                    .sendMessage(
                            miniMessage.deserialize(
                                    s,
                                    Placeholder.parsed(
                                            "prefix", configManager.languageRecord().prefix())));
        }
    }

    @Command("party help")
    public void helpCommand(CommandSourceStack stack) {

        List<String> unparsed = configManager.languageRecord().partyHelp();

        for (String s : unparsed) {

            stack.getSender()
                    .sendMessage(
                            miniMessage.deserialize(
                                    s,
                                    Placeholder.parsed(
                                            "prefix", configManager.languageRecord().prefix())));
        }
    }
}
