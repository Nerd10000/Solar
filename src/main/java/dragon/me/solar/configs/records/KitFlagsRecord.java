package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record KitFlagsRecord(
        @Setting("max-health") float maxHealth,
        @Setting("natural-regeneration") boolean naturalRegeneration,
        @Setting("natural-saturation") boolean naturalSaturation,
        @Setting("hunger-loss") boolean hungerLoss,
        @Setting("pearl-cooldown") int pearlCooldown,
        @Setting("golden-apple-cooldown") int goldenAppleCooldown,
        @Setting("windcharge-cooldown") int windChargeCooldown,
        @Setting("prevent-block-place") boolean preventBlockPlace,
        @Setting("prevent-block-break") boolean preventBlockBreak,
        @Setting("prevent-item-drop") boolean preventItemDrop,
        @Setting("prevent-movement-before-start") boolean preventMovementBeforeStart) {

    public static final KitFlagsRecord DEFAULT =
            new KitFlagsRecord(20, true, true, true, -1, -1, -1, false, false, false, false);
}
