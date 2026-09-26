package dragon.me.solar.commands.args.kit;

import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitSetEffectsArg {
    private final KitCommandContext context;

    public KitSetEffectsArg(KitCommandContext context) {
        this.context = context;
    }

    @Command("kit set-effects <id>")
    @Permission("solar.kit.manage.set-effects")
    public void setEffects(CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryKit kit = context.kitManager().getKit(id);
        if (kit == null) {
            context.send(player, context.messages().language().kitNotFound(), Placeholder.parsed("kit", id));
            return;
        }

        kit.effectList = new ArrayList<>(player.getActivePotionEffects());
        context.send(player, context.messages().language().kitEffectsSet(), Placeholder.parsed("kit", id));
    }
}
