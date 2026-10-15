package dragon.me.solar.kit;

import dragon.me.solar.Solar;
import dragon.me.solar.database.models.kits.KitEditingSession;
import dragon.me.solar.utils.InventoryUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

public class KitService {
    private final KitManager kitManager;
    private final Map<UUID, KitEditingSession> editingSessions = new HashMap<>();

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

    public @Nullable KitEditingSession getEditingSession(UUID uuid) {
        return editingSessions.get(uuid);
    }

    public void addEditingSession(UUID uuid, KitEditingSession session) {
        editingSessions.put(uuid, session);
    }

    public void removeEditingSession(UUID uuid) {
        editingSessions.remove(uuid);
    }

    public Map<UUID, KitEditingSession> getEditingSessions() {
        return editingSessions;
    }

    public void applyLayoutKit(Player player, String kitId) {
        Solar.databaseManager.getLayoutById(player.getUniqueId(), kitId).thenAccept(kitLayout -> Bukkit.getScheduler()
                .runTask(Solar.instance, () -> {
                    if (!player.isOnline()) {
                        return;
                    }

                    if (kitLayout == null) {
                        InMemoryKit kit = Solar.kitService.getKit(kitId);
                        if (kit != null) {
                            applyKit(player, kit);
                        }
                        return;
                    }

                    try {
                        player.getInventory().clear();
                        player.getInventory().setStorageContents(InventoryUtils.deserialize(kitLayout.getContents()));
                        player.getInventory().setArmorContents(InventoryUtils.deserialize(kitLayout.getArmor()));
                        player.getInventory().setItemInOffHand(InventoryUtils.deserialize(kitLayout.getOffhand())[0]);
                    } catch (Exception e) {
                        Solar.instance
                                .getLogger()
                                .log(
                                        Level.SEVERE,
                                        "Failed to apply kit layout for " + player.getUniqueId() + " and kit " + kitId,
                                        e);
                    }
                }));
    }

    public InMemoryKit getKit(String kitId) {
        return kitManager.getKit(kitId);
    }
}
