package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class SpectateCommand {

    @Command("spectate <player>")
    public void spectate(CommandSourceStack stack, @Argument("player") Player player) {

        if (stack.getSender() instanceof Player p) {

            InMemoryMatch match = Solar.matchManager.getMatchByMember(player.getUniqueId());

            if (match == null) {

                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().matchNotFound(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix()),
                                Placeholder.parsed("member", player.getName())));
                return;
            }

            if (Solar.matchManager.isSpectatingAlready(p.getUniqueId())) {

                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().alreadyInSprectator(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix())));

                return;
            }
            p.teleport(player.getLocation());
            p.setGameMode(GameMode.SPECTATOR);

            match.getSpectatorList().add(p.getUniqueId());

            match.broadcast(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().spectatorJoined(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("spectator", p.getName())));

        } else {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));

            return;
        }
    }

    @Command("spectate leave <player>")
    public void leave(CommandSourceStack stack, @Argument("player") Player player) {

        if (stack.getSender() instanceof Player p) {

            InMemoryMatch match = Solar.matchManager.getMatchByMember(player.getUniqueId());

            if (match == null) {

                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().matchNotFound(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix()),
                                Placeholder.parsed("member", player.getName())));
                return;
            }

            if (!Solar.matchManager.isSpectatingAlready(p.getUniqueId())) {

                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().alreadyInSprectator(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix())));

                return;
            }

            p.setGameMode(GameMode.SURVIVAL);

            match.getSpectatorList().remove(p.getUniqueId());

            match.broadcast(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().spectatorLeft(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("spectator", p.getName())));

            p.teleport(Solar.configManager.getLobbyLocation());

        } else {
            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));

            return;
        }
    }
}
