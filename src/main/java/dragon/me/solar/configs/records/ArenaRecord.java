package dragon.me.solar.configs.records;

import org.bukkit.Location;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record ArenaRecord(
        @Setting("name") String name,
        @Setting("min-x") int minX,
        @Setting("min-Z") int minZ,
        @Setting("min-y") int minY,
        @Setting("max-y") int maxY,
        @Setting("max-x") int maxX,
        @Setting("max-z") int maxZ,
        @Setting("spawn-1") LocationRecord spawn1,
        @Setting("spawn-2") LocationRecord spawn2,
        @Setting("schem-path") String schematicPath) {

    public boolean contains(Location location) {
        return location.getBlockX() >= minX
                && location.getBlockX() <= maxX
                && location.getBlockY() >= minY
                && location.getBlockY() <= maxY
                && location.getBlockZ() >= minZ
                && location.getBlockZ() <= maxZ;
    }
}
