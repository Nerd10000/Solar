package dragon.me.solar.arena;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import java.util.ArrayDeque;
import java.util.Queue;

public class GridManager {

    private final Queue<Integer> freeSlots = new ArrayDeque<>();
    private int nextSlot = 0;

    public GridManager() {}

    public BlockVector3 getCenter(int i) {
        int row = i / Solar.configManager.settingsRecord().arena().column();
        int column = i % Solar.configManager.settingsRecord().arena().column();

        return BlockVector3.at(
                column * Solar.configManager.settingsRecord().arena().offset(),
                Solar.configManager.settingsRecord().arena().y(),
                row * Solar.configManager.settingsRecord().arena().offset());
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
