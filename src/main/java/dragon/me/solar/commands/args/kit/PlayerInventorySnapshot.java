package dragon.me.solar.commands.args.kit;

import java.util.Arrays;
import org.bukkit.inventory.ItemStack;

public record PlayerInventorySnapshot(ItemStack[] contents, ItemStack[] armor, ItemStack offhand) {
    public PlayerInventorySnapshot {
        contents = cloneItems(contents);
        armor = cloneItems(armor);
        offhand = offhand == null ? null : offhand.clone();
    }

    private static ItemStack[] cloneItems(ItemStack[] items) {
        return Arrays.stream(items)
                .map(item -> item == null ? null : item.clone())
                .toArray(ItemStack[]::new);
    }
}
