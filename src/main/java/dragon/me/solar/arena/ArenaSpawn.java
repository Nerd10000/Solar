package dragon.me.solar.arena;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record ArenaSpawn(
        @Setting("x") double x,
        @Setting("y") double y,
        @Setting("z") double z,
        @Setting("yaw") float yaw,
        @Setting("pitch") float pitch) {

    public static ArenaSpawn fromAbsolute(Location location, BlockVector3 origin) {
        return new ArenaSpawn(
                location.getX() - origin.x(),
                location.getY() - origin.y(),
                location.getZ() - origin.z(),
                location.getYaw(),
                location.getPitch());
    }

    public Location toLocation(World world, BlockVector3 origin) {
        return new Location(world, origin.x() + x, origin.y() + y, origin.z() + z, yaw, pitch);
    }
}
