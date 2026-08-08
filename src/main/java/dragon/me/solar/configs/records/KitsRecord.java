package dragon.me.solar.configs.records;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record KitsRecord(@Setting("kits") Map<String, KitRecord> kits) {

    public static final KitsRecord DEFAULTS = new KitsRecord(new HashMap<>());

    public KitsRecord addKit(KitRecord record) {
        Map<String, KitRecord> temp = new HashMap<>(kits());

        temp.put(record.id(), record);

        return new KitsRecord(temp);
    }
}
