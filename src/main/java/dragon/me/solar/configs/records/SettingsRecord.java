package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record SettingsRecord(
        @Setting("arena") GlobalArenaSettingsRecord arena,
        @Setting("duel-request-expire-time") long duelRequestExpireTime) {

    public static final SettingsRecord DEFAULTS =
            new SettingsRecord(GlobalArenaSettingsRecord.DEFAULTS, 120000);
}
