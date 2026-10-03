package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record QueueRecord(
        @Setting("name") String name,
        @Setting("kit") String kit,
        @Setting("isEnabled") boolean isEnabled,
        @Setting("flags") QueueFlagsRecord flags) {

    public static final QueueRecord DEFAULTS = new QueueRecord("", "", false, QueueFlagsRecord.DEFAULT);
}
