package dragon.me.solar.configs.records;

import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record LocationRecord(
        @Setting("x") double x,
        @Setting("y") double y,
        @Setting("z") double z,
        @Setting("yaw") float yaw,
        @Setting("pitch") float pitch) {

    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static LocationRecord from(Location location) {
        return new LocationRecord(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }
}
