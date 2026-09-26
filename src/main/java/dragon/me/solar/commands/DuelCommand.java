package dragon.me.solar.commands;

import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.kit.KitManager;
import java.util.List;
import org.incendo.cloud.annotations.suggestion.Suggestions;

/** Compatibility type that provides shared duel argument suggestions. */
public class DuelCommand {
    private final ArenaManager arenaManager;
    private final KitManager kitManager;

    public DuelCommand(ArenaManager arenaManager, KitManager kitManager) {
        this.arenaManager = arenaManager;
        this.kitManager = kitManager;
    }

    @Suggestions("arenas")
    public List<String> arenaSuggestions() {
        return arenaManager.getArenas().keySet().stream().toList();
    }

    @Suggestions("kits")
    public List<String> kitSuggestions() {
        return kitManager.getKits().keySet().stream().toList();
    }
}
