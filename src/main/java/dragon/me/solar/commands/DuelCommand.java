package dragon.me.solar.commands;

import dragon.me.solar.Solar;
import dragon.me.solar.duel.record.DuelInviteRecord;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;

public class DuelCommand {

    @Command("duel <player> <kit>")
    public void duel(CommandSourceStack stack, @Argument("player") Player player, @Argument("kit") String kit, @Flag("rounds") @Default("1") int rounds, @Flag("map") @Default("random") String map){

        if (!(stack.getSender() instanceof Player sender)){

            stack.getSender().sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().consoleCantRun(),
                            Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix())
                    )
            );
            return;
        }

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(kit);

        if (inMemoryKit == null){
            player.sendMessage(
                    Solar.miniMessage.deserialize(

                            Solar.configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", kit)

                    )
            );
            return;
        }

        DuelInviteRecord record = new DuelInviteRecord(
                sender.getUniqueId(),
                player.getUniqueId(),

                kit,
                map,
                rounds,
                System.currentTimeMillis()
        );

        Solar.duelInviteManager.add(record);

        sender.sendMessage(
                Solar.miniMessage.deserialize(

                        Solar.configManager.languageRecord().duelRequestSent(),
                        Placeholder.parsed("player", player.getName()),
                        Placeholder.parsed("rounds", String.valueOf(rounds)),
                        Placeholder.parsed("map", map),
                        Placeholder.parsed("kit", kit),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix())

                )
        );

        player.sendMessage(
                Solar.miniMessage.deserialize(

                        Solar.configManager.languageRecord().duelRequestReceived(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("player", sender.getName()),
                        Placeholder.parsed("rounds", String.valueOf(rounds)),
                        Placeholder.parsed("kit", kit),
                        Placeholder.parsed("map", map)

                )
        );

    }
}
