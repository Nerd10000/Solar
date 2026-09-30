package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record RatingSettingsRecord(
        @Setting("default-elo") int defaultElo,
        @Setting("k-factor") int kFactor) {

    public static RatingSettingsRecord DEFAULT = new RatingSettingsRecord(1000, 32);
}
