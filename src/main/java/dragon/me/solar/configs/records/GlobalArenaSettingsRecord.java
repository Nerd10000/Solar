package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record GlobalArenaSettingsRecord(
        @Setting("offset") int offset,
        @Setting("y-coordinate") int y,
        @Setting("column") int column) {

    public static final GlobalArenaSettingsRecord DEFAULTS =
            new GlobalArenaSettingsRecord(512, 64, 100);
}
