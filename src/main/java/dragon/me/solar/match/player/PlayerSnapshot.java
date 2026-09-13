package dragon.me.solar.match.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerSnapshot {

    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final ItemStack offhand;
    private final List<PotionEffect> effects;

    public PlayerSnapshot(
            ItemStack[] contents,
            ItemStack[] armor,
            ItemStack offhand,
            Collection<PotionEffect> effects) {
        this.contents = cloneArray(contents);
        this.armor = cloneArray(armor);
        this.offhand =
                offhand != null && offhand.getType() != Material.AIR ? offhand.clone() : null;
        this.effects = new ArrayList<>(effects);
    }

    public static PlayerSnapshot capture(Player player) {
        return new PlayerSnapshot(
                player.getInventory().getStorageContents(),
                player.getInventory().getArmorContents(),
                player.getInventory().getItemInOffHand(),
                player.getActivePotionEffects());
    }

    public void restore(Player player) {
        player.getInventory().clear();

        if (contents != null) {
            player.getInventory().setStorageContents(cloneArray(contents));
        }

        if (armor != null) {
            player.getInventory().setArmorContents(cloneArray(armor));
        }

        if (offhand != null) {
            player.getInventory().setItemInOffHand(offhand.clone());
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }

    private static ItemStack[] cloneArray(ItemStack[] source) {
        if (source == null) {
            return new ItemStack[0];
        }

        ItemStack[] cloned = new ItemStack[source.length];
        for (int i = 0; i < source.length; i++) {
            cloned[i] = source[i] != null ? source[i].clone() : null;
        }
        return cloned;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ItemStack getOffhand() {
        return offhand;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }
}
