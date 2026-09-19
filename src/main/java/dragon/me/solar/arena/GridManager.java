package dragon.me.solar.arena;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.configs.ConfigManager;
import java.util.ArrayDeque;
import java.util.Queue;

public class GridManager {

    private final Queue<Integer> freeSlots = new ArrayDeque<>();
    private final ConfigManager configManager;
    private int nextSlot = 0;

    public GridManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public BlockVector3 getCenter(int i) {
        int row = i / configManager.settingsRecord().arena().column();
        int column = i % configManager.settingsRecord().arena().column();

        return BlockVector3.at(
                column * configManager.settingsRecord().arena().offset(),
                configManager.settingsRecord().arena().y(),
                row * configManager.settingsRecord().arena().offset());
    }

    public int allocate() {
        if (!freeSlots.isEmpty()) {
            return freeSlots.poll();
        }
        return nextSlot++;
    }

    public void free(int slot) {
        freeSlots.offer(slot);
    }
}
