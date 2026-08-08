/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package dragon.me.solar.configs.utils;

import java.lang.reflect.Type;
import java.util.Base64;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {

        String value = node.getString();

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return ItemStack.deserializeBytes(DECODER.decode(value));
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            throw new SerializationException("Failed to deserialize ItemStack");
        }
    }

    @Override
    public void serialize(Type type, ItemStack item, ConfigurationNode node)
            throws SerializationException {

        if (item == null) {
            node.raw(null);
            return;
        }

        String value = ENCODER.encodeToString(item.serializeAsBytes());

        node.raw(value);
    }
}
