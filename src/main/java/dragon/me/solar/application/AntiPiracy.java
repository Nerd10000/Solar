package dragon.me.solar.application;

import dragon.me.solar.Solar;
import org.bukkit.Bukkit;

public class AntiPiracy {

    private static final String fromBBB = "%%__BUILTBYBIT__%%";

    public static void check(boolean devBuild) {

        if (!fromBBB.equalsIgnoreCase("true") && !devBuild) {
            Solar.instance.getLogger().severe("Solar could not be started!");
            Solar.instance.getLogger().severe("Solar could not verify this installation.");
            Solar.instance.getLogger().severe("This copy does not appear to be an authorized BuiltByBit distribution.");
            Solar.instance.getLogger().severe("Please download Solar from the official BuiltByBit resource page.");
            Solar.instance
                    .getLogger()
                    .severe(
                            "Solar is developed independently in the developer's free time. Please support the project by purchasing an official copy. ❤️");

            Bukkit.getPluginManager().disablePlugin(Solar.instance);
        }
    }
}
