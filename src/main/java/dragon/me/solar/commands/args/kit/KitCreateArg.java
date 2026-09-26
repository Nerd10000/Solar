package dragon.me.solar.commands.args.kit;

import dragon.me.solar.configs.records.KitFlagsRecord;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitCreateArg {
    private final KitCommandContext context;

    public KitCreateArg(KitCommandContext context) {
        this.context = context;
    }

    @Command("kit create <id>")
    @Permission("solar.kit.manage.create")
    public void create(CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryKit kit = new InMemoryKit(id, null, List.of(), null, null, KitFlagsRecord.DEFAULT);
        String message = context.kitManager().create(kit)
                ? context.messages().language().kitCreated()
                : context.messages().language().kitExists();
        context.send(player, message, Placeholder.parsed("kit", id));
    }
}
