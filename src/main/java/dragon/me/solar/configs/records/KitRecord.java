package dragon.me.solar.configs.records;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record KitRecord(
        @Setting("id") String id,
        @Setting("potion-effects") List<PotionEffect> potionEffects,
        @Setting("items") ItemStack[] items,
        @Setting("armor") ItemStack[] armor,
        @Setting("offhand") ItemStack offhand,
        @Setting("flags") KitFlagsRecord flags) {

    public static final KitRecord DEFAULTS =
            new KitRecord(
                    "Unknown",
                    new ArrayList<>(),
                    new ItemStack[] {},
                    new ItemStack[] {},
                    null,
                    KitFlagsRecord.DEFAULT);
}
