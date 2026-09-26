package dragon.me.solar.commands.args.kit;

import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitSetItemsArg {
    private final KitCommandContext context;

    public KitSetItemsArg(KitCommandContext context) {
        this.context = context;
    }

    @Command("kit set-items <id>")
    @Permission("solar.kit.manage.set-items")
    public void setItems(CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String id) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryKit kit = context.kitManager().getKit(id);
        if (kit == null) {
            sendKitNotFound(player, id);
            return;
        }

        kit.items = player.getInventory().getStorageContents();
        kit.armor = player.getInventory().getArmorContents();
        kit.offhand = player.getInventory().getItemInOffHand().getType() == Material.AIR
                ? null
                : player.getInventory().getItemInOffHand();
        context.send(player, context.messages().language().kitItemsSet(), Placeholder.parsed("kit", id));
    }

    private void sendKitNotFound(Player player, String id) {
        context.send(player, context.messages().language().kitNotFound(), Placeholder.parsed("kit", id));
    }
}
