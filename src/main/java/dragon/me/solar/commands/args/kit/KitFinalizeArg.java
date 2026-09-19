package dragon.me.solar.commands.args.kit;

import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitFinalizeArg {
    private final KitCommandContext context;

    public KitFinalizeArg(KitCommandContext context) {
        this.context = context;
    }

    @Command("kit finalize <id>")
    @Permission("solar.kit.manage.finalize")
    public void finalizeKit(
            CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryKit kit = context.kitManager().getKit(id);
        if (kit == null) {
            context.send(
                    player,
                    context.messages().language().kitNotFound(),
                    Placeholder.parsed("kit", id));
            return;
        }

        context.configManager().registerKit(kit.toRecord());
        context.send(
                player,
                context.messages().language().kitFinalized(),
                Placeholder.parsed("kit", id));
    }
}
