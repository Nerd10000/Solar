package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record SoundRecords(
        @Setting("countdown-sound") String countdownSound,
        @Setting("start-sound") String startSound,
        @Setting("lose-sound") String lostSound,
        @Setting("dead-sound") String deadSound,
        @Setting("won-sound") String wonSound) {}
