package dragon.me.solar.kit;

import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.configs.records.KitsRecord;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

public class KitManager {

    public Map<String, InMemoryKit> KITS = new HashMap<>();

    public KitManager() {}

    public void load(KitsRecord record) {

        for (Map.Entry<String, KitRecord> e : record.kits().entrySet()) {

            KITS.put(
                    e.getKey(),
                    new InMemoryKit(
                            e.getValue().id(),
                            e.getValue().items(),
                            e.getValue().potionEffects(),
                            e.getValue().armor(),
                            e.getValue().offhand(),
                            e.getValue().flags()));
        }
    }

    public boolean create(InMemoryKit kit) {

        if (KITS.containsKey(kit.kitId)) {
            return false;
        }

        KITS.put(kit.kitId, kit);
        return true;
    }

    public @Nullable InMemoryKit getKit(String kitId) {

        return KITS.get(kitId);
    }

    public void applyKit(Player player, InMemoryKit kit) {
        if (kit == null) {
            return;
        }

        player.getInventory().clear();

        if (kit.items != null && kit.items.length > 0) {
            player.getInventory().setStorageContents(kit.items);
        }

        if (kit.armor != null && kit.armor.length > 0) {
            player.getInventory().setArmorContents(kit.armor);
        }

        if (kit.offhand != null) {
            player.getInventory().setItemInOffHand(kit.offhand);
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (kit.effectList != null) {
            for (PotionEffect effect : kit.effectList) {
                player.addPotionEffect(effect);
            }
        }
    }
}
