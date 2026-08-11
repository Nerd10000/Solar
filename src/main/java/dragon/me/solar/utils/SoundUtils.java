package dragon.me.solar.utils;

import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class SoundUtils {

    private SoundUtils() {}

    public static void playConfiguredSound(Player player, String configuredSound, Sound fallback) {
        if (player == null || !player.isOnline()) {
            return;
        }

        Sound sound = resolveSound(configuredSound, fallback);

        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    private static Sound resolveSound(String configuredSound, Sound fallback) {
        if (configuredSound == null || configuredSound.isBlank()) {
            return fallback;
        }

        String soundKey = configuredSound.trim().toLowerCase();

        try {
            Sound sound = Registry.SOUNDS.get(Key.key(soundKey));

            return sound != null ? sound : fallback;
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }
}
