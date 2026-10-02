package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record QueueFlagsRecord(
        @Setting("weight") String weight,
        @Setting("team-size") int teamSize,
        @Setting("allow-parties") boolean allowParties,
        @Setting("min-rating") int minRating,
        @Setting("permission") String permission,
        @Setting("min-party-size") int minPartySize,
        @Setting("max-party-size") int maxPartySize) {

    public static final QueueFlagsRecord DEFAULT = new QueueFlagsRecord("None", 1, false, 0, "", 0, 0);
}
