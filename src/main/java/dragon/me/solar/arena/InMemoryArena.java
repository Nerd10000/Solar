package dragon.me.solar.arena;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.configs.records.ArenaRecord;

public class InMemoryArena {

    public String name;
    public int minX, minY, minZ, maxX, maxY, maxZ;
    public ArenaSpawn spawn1, spawn2;
    public boolean isMinBorderSet, isMaxBorderSet, isSpawns1Set, isSpawn2Set;

    public InMemoryArena(String name) {
        this.name = name;
    }

    public boolean canBeFinalized() {

        return isMinBorderSet && isMaxBorderSet && isSpawn2Set && isSpawns1Set;
    }

    public BlockVector3 getPasteOrigin() {
        return BlockVector3.at((minX + maxX) / 2, minY, (minZ + maxZ) / 2);
    }

    public ArenaRecord toRecord() {

        return new ArenaRecord(
                name,
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ,
                spawn1,
                spawn2,
                "schem/" + name + ".schem");
    }
}
