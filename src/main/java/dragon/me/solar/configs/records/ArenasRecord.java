package dragon.me.solar.configs.records;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record ArenasRecord(@Setting("arenas") Map<String, ArenaRecord> arenas) {

    public static final ArenasRecord DEFAULTS = new ArenasRecord(Map.of());

    public ArenasRecord addArena(ArenaRecord record) {

        Map<String, ArenaRecord> temp = new HashMap<>(arenas());

        temp.put(record.name(), record);

        return new ArenasRecord(temp);
    }
}
