package dragon.me.solar.arena;

import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.ArenasRecord;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    private final Map<String, InMemoryArena> ARENAS = new HashMap<>();

    public void create(InMemoryArena arena) {
        if (ARENAS.containsKey(arena.name)) {
            throw new RuntimeException("Arena already exists!");
        }
        ARENAS.put(arena.name, arena);
    }

    public InMemoryArena getArena(String name) {
        return ARENAS.get(name);
    }

    public Map<String, InMemoryArena> getArenas() {
        return Map.copyOf(ARENAS);
    }

    public void load(ArenasRecord record) {
        for (Map.Entry<String, ArenaRecord> entry : record.arenas().entrySet()) {
            InMemoryArena inMemoryArena = new InMemoryArena(entry.getKey());
            inMemoryArena.minX = entry.getValue().minX();
            inMemoryArena.minY = entry.getValue().minY();
            inMemoryArena.minZ = entry.getValue().minZ();
            inMemoryArena.maxX = entry.getValue().maxX();
            inMemoryArena.maxY = entry.getValue().maxY();
            inMemoryArena.maxZ = entry.getValue().maxZ();
            inMemoryArena.spawn1 = entry.getValue().spawn1();
            inMemoryArena.spawn2 = entry.getValue().spawn2();
            inMemoryArena.isSpawns1Set = true;
            inMemoryArena.isSpawn2Set = true;
            inMemoryArena.isMaxBorderSet = true;
            inMemoryArena.isMinBorderSet = true;
            create(inMemoryArena);
        }
    }

    public String resolveArenaName(String map) {
        if (map.equalsIgnoreCase("random")) {
            if (getArenas().isEmpty()) {
                return null;
            }
            return getArenas().values().stream()
                    .skip(ThreadLocalRandom.current().nextInt(getArenas().size()))
                    .findFirst()
                    .orElseThrow()
                    .name;
        }
        if (!getArenas().containsKey(map)) {
            return null;
        }
        return map;
    }
}
