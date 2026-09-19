package dragon.me.solar.commands;

import dragon.me.solar.kit.KitManager;
import java.util.List;
import org.incendo.cloud.annotations.suggestion.Suggestions;

/** Compatibility type that provides shared kit argument suggestions. */
public final class KitCommand {
    private final KitManager kitManager;

    public KitCommand(KitManager kitManager) {
        this.kitManager = kitManager;
    }

    @Suggestions("kits")
    public List<String> kitSuggestions() {
        return kitManager.getKits().keySet().stream().toList();
    }
}
