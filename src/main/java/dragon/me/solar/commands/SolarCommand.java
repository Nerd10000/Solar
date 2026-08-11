package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.records.LobbyRecord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class SolarCommand {

    @Command("solar setlobby")
    @Permission("solar.setlobby")
    public void setLobby(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        LobbyRecord lobby =
                new LobbyRecord(
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch(),
                        player.getWorld().getName());

        Solar.configManager.setLobbyLocation(lobby);

        player.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().lobbySet(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));
    }

    @Command("solar reload")
    @Permission("solar.reload")
    public void reload(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player player)) {
            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        try {
            Solar.configManager.reloadAll();
            player.sendMessage(
                    Solar.miniMessage.deserialize(
                            "<prefix><gray>Configuration reloaded.</gray>",
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
        } catch (Exception e) {
            player.sendMessage(
                    Solar.miniMessage.deserialize(
                            "<prefix><red>Failed to reload configuration.</red>",
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
        }
    }
}
