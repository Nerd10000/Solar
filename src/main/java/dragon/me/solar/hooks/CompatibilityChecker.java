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
    }

    public boolean isCompatibleWith(Compatibilities c) {

        if (compatibilitiesList.contains(c)) {
            return true;
        } else {
            return false;
        }
    }
}
