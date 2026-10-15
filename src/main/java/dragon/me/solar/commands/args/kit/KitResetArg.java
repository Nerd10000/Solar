package dragon.me.solar.commands.args.kit;

import dragon.me.solar.database.models.kits.KitEditingSession;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitResetArg {

    private final KitCommandContext context;

    public KitResetArg(KitCommandContext context) {
        this.context = context;
    }

    @Command("kit reset <id>")
    @Permission("solar.kit.reset")
    public void resetKit(CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String kitId) {

        if (!(stack.getSender() instanceof Player p)) {

            context.sendConsoleError(stack);
            return;
        }

        if (!context.kitService().getEditingSessions().containsKey(p.getUniqueId())) {

            context.send(p, context.messages().language().playerNotEditingKit());
            return;
        }

        InMemoryKit kit = context.kitManager().getKit(kitId);

        if (kit == null) {
            context.send(p, context.messages().language().kitNotFound(), Placeholder.parsed("kit", kitId));
            return;
        }

        KitEditingSession session = context.kitService().getEditingSessions().get(p.getUniqueId());

        if (session == null) {
            context.send(p, context.messages().language().playerNotEditingKit());
            return;
        }

        PlayerInventorySnapshot snapshot = session.snapshot();

        p.getInventory().clear();

        p.teleport(context.configManager().getLobbyLocation());
        p.getInventory().setArmorContents(snapshot.armor());
        p.getInventory().setStorageContents(snapshot.contents());
        p.getInventory().setItemInOffHand(snapshot.offhand());

        context.kitService().removeEditingSession(p.getUniqueId());
    }
}
