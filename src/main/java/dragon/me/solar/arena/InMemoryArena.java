package dragon.me.solar.arena;

import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.LocationRecord;
import org.bukkit.Location;

public class InMemoryArena {

    public String name;
    public int minX, minY, minZ, maxX, maxY, maxZ;
    public Location spawn1, spawn2;
    public boolean isMinBorderSet, isMaxBorderSet, isSpawns1Set, isSpawn2Set;

    public InMemoryArena(String name) {
        this.name = name;
    }

    public boolean canBeFinalized() {

        return isMinBorderSet && isMaxBorderSet && isSpawn2Set && isSpawns1Set;
    }

    public ArenaRecord toRecord() {

        return new ArenaRecord(
                name,
                minX,
                minY,
                minZ,
                maxY,
                maxX,
                maxZ,
                new LocationRecord(
                        spawn1.getX(),
                        spawn1.getY(),
                        spawn1.getZ(),
                        spawn1.getYaw(),
                        spawn1.getPitch()),
                new LocationRecord(
                        spawn2.getX(),
                        spawn2.getY(),
                        spawn2.getZ(),
                        spawn2.getYaw(),
                        spawn2.getPitch()),
                "schems/" + name + ".schem");
    }
}
