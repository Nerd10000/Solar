package dragon.me.solar.utils;

import java.io.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryUtils {

    public static byte[] serialize(ItemStack[] itemStacks) throws IOException {
        try (var out = new ByteArrayOutputStream();
                var data = new DataOutputStream(out)) {

            data.writeInt(1); // Format version
            data.writeInt(itemStacks.length);

            for (var item : itemStacks) {
                if (item == null || item.getType() == Material.AIR) {
                    data.writeInt(-1);
                    continue;
                }

                byte[] itemBytes = item.serializeAsBytes();
                data.writeInt(itemBytes.length);
                data.write(itemBytes);
            }

            return out.toByteArray();
        }
    }

    public static @Nullable ItemStack[] deserialize(byte[] bytes) throws IOException {
        try (var in = new ByteArrayInputStream(bytes);
                var data = new DataInputStream(in)) {

            int version = data.readInt();
            if (version != 1) {
                throw new IOException("Unsupported format version: " + version);
            }

            int length = data.readInt();
            if (length < 0 || length > 256) {
                throw new IOException("Invalid inventory size: " + length);
            }

            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                int itemLength = data.readInt();

                if (itemLength == -1) {
                    items[i] = null;
                    continue;
                }

                if (itemLength < 0 || itemLength > data.available()) {
                    throw new IOException("Invalid item data length: " + itemLength);
                }

                byte[] itemBytes = data.readNBytes(itemLength);
                items[i] = ItemStack.deserializeBytes(itemBytes);
            }

            if (data.available() != 0) {
                throw new IOException("Unexpected trailing data");
            }

            return items;
        }
    }
}
