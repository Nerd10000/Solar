package dragon.me.solar.commands.args.kit;

import dragon.me.solar.Solar;
import dragon.me.solar.database.models.kits.KitEditingSession;
import dragon.me.solar.database.models.kits.KitLayout;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.utils.InventoryUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.IOException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class KitSaveArg {

    private final KitCommandContext kitContext;

    public KitSaveArg(KitCommandContext kitContext) {
        this.kitContext = kitContext;
    }

    @Command("kit save <id>")
    @Permission("solar.kit.edit")
    public void saveKit(CommandSourceStack stack, @Argument(value = "id", suggestions = "kits") String kitId) {

        if (!(stack.getSender() instanceof Player p)) {

            kitContext.sendConsoleError(stack);
            return;
        }

        InMemoryKit kit = kitContext.kitManager().getKit(kitId);

        if (kit == null) {
            kitContext.send(p, kitContext.messages().language().kitNotFound(), Placeholder.parsed("kit", kitId));
            return;
        }

        if (!kitContext.kitService().getEditingSessions().containsKey(p.getUniqueId())) return; // TODO

        KitEditingSession session = kitContext.kitService().getEditingSession(p.getUniqueId());

        if (session == null) {
            kitContext.send(p, kitContext.messages().language().playerNotEditingKit());
            return;
        }

        PlayerInventorySnapshot snapshot = session.snapshot();

        KitLayout kitLayout = new KitLayout();

        try {
            kitLayout.setPlayerId(p.getUniqueId().toString());
            kitLayout.setKitId(kitId);

            kitLayout.setArmor(InventoryUtils.serialize(p.getInventory().getArmorContents()));
            kitLayout.setContents(InventoryUtils.serialize(p.getInventory().getStorageContents()));

            kitLayout.setOffhand(
                    InventoryUtils.serialize(new ItemStack[] {p.getInventory().getItemInOffHand()}));

            Solar.databaseManager.removeLayout(p.getUniqueId(), kitId);
            Solar.databaseManager.updateLayout(kitLayout);

            p.getInventory().clear();

            p.getInventory().setItemInOffHand(snapshot.offhand());
            p.getInventory().setArmorContents(snapshot.armor());
            p.getInventory().setStorageContents(snapshot.contents());

            kitContext.kitService().removeEditingSession(p.getUniqueId());
            p.teleport(kitContext.configManager().getLobbyLocation());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
