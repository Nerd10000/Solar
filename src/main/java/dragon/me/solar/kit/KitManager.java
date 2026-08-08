package dragon.me.solar.kit;

import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.configs.records.KitsRecord;
import java.util.HashMap;
import java.util.Map;
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
                            e.getValue().offhand()));
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
}
