package dragon.me.solar.configs.utils;

import java.lang.reflect.Type;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class PotionEffectSerializer implements TypeSerializer<PotionEffect> {

    @Override
    public PotionEffect deserialize(Type type, ConfigurationNode node)
            throws SerializationException {
        if (node.virtual()) {
            return null;
        }

        PotionEffectType potionEffectType =
                PotionEffectType.getByName(node.node("type").getString());

        if (potionEffectType == null) {

            throw new SerializationException("Not a valid PotionEffectType.");
        }

        int duration = node.node("duration").getInt();

        int amplifier = node.node("amplifier").getInt();
        boolean ambient = node.node("ambient").getBoolean(false);
        boolean particles = node.node("particles").getBoolean(true);

        boolean icon = node.node("icon").getBoolean(true);

        return new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);
    }

    @Override
    public void serialize(Type type, @Nullable PotionEffect effect, ConfigurationNode node)
            throws SerializationException {
        if (effect == null) {
            node.raw(null);
            return;
        }

        node.node("type").set(effect.getType().getName());
        node.node("duration").set(effect.getDuration());
        node.node("amplifier").set(effect.getAmplifier());

        node.node("ambient").set(effect.isAmbient());
        node.node("particles").set(effect.hasParticles());
        node.node("icon").set(effect.hasIcon());
    }
}
