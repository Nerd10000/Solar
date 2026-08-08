package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.Nullable;

public final class PingCommand {

    @Command("ping [player]")
    public void ping(CommandSourceStack stack, @Argument("player") @Nullable Player player) {
        if (!(stack.getSender() instanceof Player playerSender)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        if (player != null) {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().pingOtherCommand(),
                            Placeholder.parsed("target", player.getName()),
                            Placeholder.parsed("ping", String.valueOf(player.getPing()))));

        } else {
            playerSender.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().pingCommand(),
                            Placeholder.unparsed("ping", String.valueOf(playerSender.getPing()))));
        }
    }
}
