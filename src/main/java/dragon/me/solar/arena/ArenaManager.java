package dragon.me.solar.arena;

import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.ArenasRecord;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class ArenaManager {

    public HashMap<String, InMemoryArena> ARENAS = new HashMap<>();

    public void create(InMemoryArena arena) {

        if (ARENAS.containsKey(arena.name)) {
            throw new RuntimeException("Arena already exists!");
        }

        ARENAS.put(arena.name, arena);
    }

    public @Nullable InMemoryArena getArena(String name) {

        return ARENAS.get(name);
    }

    public void load(ArenasRecord record) {

        for (Map.Entry<String, ArenaRecord> entry : record.arenas().entrySet()) {

            ArenaRecord arenaRecord = entry.getValue();

            InMemoryArena inMemoryArena = new InMemoryArena(entry.getKey());

            inMemoryArena.minX = arenaRecord.minX();
            inMemoryArena.minY = arenaRecord.minY();
            inMemoryArena.minZ = arenaRecord.minZ();

            inMemoryArena.maxX = arenaRecord.maxX();
            inMemoryArena.maxY = arenaRecord.maxY();
            inMemoryArena.maxZ = arenaRecord.maxZ();

            inMemoryArena.spawn1 = arenaRecord.spawn1().toBukkitLocation(null);
            inMemoryArena.spawn2 = arenaRecord.spawn2().toBukkitLocation(null);

            inMemoryArena.isSpawns1Set = true;
            inMemoryArena.isSpawn2Set = true;
            inMemoryArena.isMaxBorderSet = true;
            inMemoryArena.isMinBorderSet = true;

            // TODO add schem path

            create(inMemoryArena);
        }
    }
}
