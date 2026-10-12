package dragon.me.solar.arena;

import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.ArenasRecord;
import dragon.me.solar.kit.InMemoryKit;
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

    public String resolveArenaName(String map, InMemoryKit kit) {
        var arenas = getArenas();

        String recoverableMap = "random";

        if (map != null) {
            recoverableMap = map;
        }

        if (recoverableMap.equalsIgnoreCase("random")) {
            var eligibleArenas = arenas.values().stream()
                    .filter(arena -> kit.flags.maps().contains("*")
                            || kit.flags.maps().stream()
                                    .anyMatch(allowedMap -> allowedMap.equalsIgnoreCase(arena.name)))
                    .toList();

            if (eligibleArenas.isEmpty()) {
                return null;
            }

            return eligibleArenas.get(ThreadLocalRandom.current().nextInt(eligibleArenas.size())).name;
        }

        var arena = arenas.get(recoverableMap);
        if (arena == null) {
            return null;
        }

        boolean allowed = kit.flags.maps().contains("*")
                || kit.flags.maps().stream().anyMatch(allowedMap -> allowedMap.equalsIgnoreCase(arena.name));
        return allowed ? arena.name : null;
    }
}
