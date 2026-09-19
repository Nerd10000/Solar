package dragon.me.solar.kit;

import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.configs.records.KitsRecord;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class KitManager {

    private final Map<String, InMemoryKit> kits = new HashMap<>();

    public KitManager() {}

    public void load(KitsRecord record) {

        for (Map.Entry<String, KitRecord> e : record.kits().entrySet()) {

            kits.put(
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

        if (kits.containsKey(kit.kitId)) {
            return false;
        }

        kits.put(kit.kitId, kit);
        return true;
    }

    public @Nullable InMemoryKit getKit(String kitId) {

        return kits.get(kitId);
    }

    public Map<String, InMemoryKit> getKits() {
        return Map.copyOf(kits);
    }
}
