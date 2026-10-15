package dragon.me.solar.commands.args.kit;

import dragon.me.solar.database.models.kits.KitEditingSession;
import dragon.me.solar.kit.InMemoryKit;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitEditArg {

    private final KitCommandContext kitContext;

    public KitEditArg(KitCommandContext kitContext) {
        this.kitContext = kitContext;
    }

    @Command("kit edit <id>")
    @Permission("solar.kit.edit")
    public void editKit(CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String kitId) {

        if (!(stack.getSender() instanceof Player p)) {

            kitContext.sendConsoleError(stack);
            return;
        }

        if (kitContext.kitService().getEditingSessions().containsKey(p.getUniqueId())) {
            kitContext.send(p, kitContext.messages().language().playerAlreadyEditingKits());
            return;
        }

        InMemoryKit kit = kitContext.kitManager().getKit(kitId);

        if (kit == null) {
            kitContext.send(p, kitContext.messages().language().kitNotFound(), Placeholder.parsed("kit", kitId));
            return;
        }

        kitContext
                .kitService()
                .addEditingSession(
                        p.getUniqueId(),
                        new KitEditingSession(
                                p.getUniqueId(),
                                kit.kitId,
                                new PlayerInventorySnapshot(
                                        p.getInventory().getStorageContents(),
                                        p.getInventory().getArmorContents(),
                                        p.getInventory().getItemInOffHand())));

        p.teleport(kitContext.configManager().getKitEditorLocation());

        p.getInventory().clear();

        p.getInventory().setArmorContents(kit.armor);
        p.getInventory().setStorageContents(kit.items);
        p.getInventory().setItemInOffHand(kit.offhand);
    }
}
