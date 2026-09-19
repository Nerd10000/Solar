package dragon.me.solar.commands.args.kit;

import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitGiveArg {
    private final KitCommandContext context;

    public KitGiveArg(KitCommandContext context) {
        this.context = context;
    }

    @Command("kit give <id> <player>")
    @Permission("solar.kit.manage.give")
    public void give(
            CommandSourceStack stack,
            @Argument(value = "id", suggestions = "kits") String id,
            @Argument("player") Player player) {
        InMemoryKit kit = context.kitManager().getKit(id);
        if (kit == null) {
            context.send(
                    stack,
                    context.messages().language().kitNotFound(),
                    Placeholder.parsed("kit", id));
            return;
        }
        context.kitService().applyKit(player, kit);
    }
}
