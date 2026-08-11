package dragon.me.solar.kit;

import dragon.me.solar.configs.records.KitFlagsRecord;
import dragon.me.solar.configs.records.KitRecord;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class InMemoryKit {
    ;

    public String kitId;
    public ItemStack[] items, armor;
    public List<PotionEffect> effectList;
    public ItemStack offhand;
    public KitFlagsRecord flags;

    public InMemoryKit(
            String kitId,
            ItemStack[] items,
            List<PotionEffect> effectList,
            ItemStack[] armor,
            ItemStack offhand,
            KitFlagsRecord flags) {
        this.kitId = kitId;

        if (items != null) {

            this.items = items;

        } else {
            this.items = new ItemStack[] {};
        }

        this.armor = armor == null ? new ItemStack[] {} : armor;

        this.offhand = offhand == null ? new ItemStack(Material.AIR) : offhand;
        this.effectList = effectList != null ? effectList : java.util.Collections.emptyList();
        this.flags = flags;
    }

    public KitRecord toRecord() {
        KitRecord record = new KitRecord(kitId, effectList, items, armor, offhand, flags);
        return record;
    }
}
