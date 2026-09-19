package dragon.me.solar.kit;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class KitService {
    private final KitManager kitManager;

    public KitService(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    public void applyKit(Player player, InMemoryKit kit) {
        if (kit == null) {
            return;
        }

        player.getInventory().clear();
        if (kit.items.length > 0) {
            player.getInventory().setStorageContents(kit.items);
        }
        if (kit.armor.length > 0) {
            player.getInventory().setArmorContents(kit.armor);
        }
        player.getInventory().setItemInOffHand(kit.offhand);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : kit.effectList) {
            player.addPotionEffect(effect);
        }
    }

    public InMemoryKit getKit(String kitId) {
        return kitManager.getKit(kitId);
    }
}
