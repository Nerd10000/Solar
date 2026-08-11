package dragon.me.solar.configs.records;

import dragon.me.solar.arena.ArenaSpawn;
import org.bukkit.Location;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record ArenaRecord(
        @Setting("name") String name,
        @Setting("min-x") int minX,
        @Setting("min-y") int minY,
        @Setting("min-Z") int minZ,
        @Setting("max-x") int maxX,
        @Setting("max-y") int maxY,
        @Setting("max-z") int maxZ,
        @Setting("spawn-1") ArenaSpawn spawn1,
        @Setting("spawn-2") ArenaSpawn spawn2,
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
