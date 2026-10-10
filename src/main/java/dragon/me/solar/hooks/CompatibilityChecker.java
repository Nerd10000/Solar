package dragon.me.solar.hooks;

import dragon.me.solar.Solar;
import java.util.ArrayList;
import java.util.List;

public class CompatibilityChecker {

    public List<Compatibilities> compatibilitiesList = new ArrayList<>();

    public CompatibilityChecker(Solar instance) {

        if (instance.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {

            compatibilitiesList.add(Compatibilities.FAWE);
        }

        if (instance.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            compatibilitiesList.add(Compatibilities.PAPI);
        }

        if (instance.getServer().getPluginManager().isPluginEnabled("Intave")) {
            compatibilitiesList.add(Compatibilities.INTAVE);
        }

        StringBuilder builder = new StringBuilder();

        for (Compatibilities c : compatibilitiesList) {

            builder.append(" " + c.name());
        }

        Solar.instance.getLogger().info("Hooks: " + builder.toString());
    }

    public boolean isCompatibleWith(Compatibilities c) {

        return compatibilitiesList.contains(c);
    }
}
