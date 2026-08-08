package dragon.me.solar;

import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.DupeArenaGenerator;
import dragon.me.solar.commands.ArenaCommand;
import dragon.me.solar.commands.DuelCommand;
import dragon.me.solar.commands.KitCommand;
import dragon.me.solar.commands.PingCommand;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.hooks.CompatibilityChecker;
import dragon.me.solar.kit.KitManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public final class Solar extends JavaPlugin {
    public static Solar instance;
    private static @NonNull PaperCommandManager<CommandSourceStack> commandManager;
    public static ConfigManager configManager;
    public static ArenaManager arenaManager;
    public static KitManager kitManager;
    public static DuelInviteManager duelInviteManager;
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static CompatibilityChecker compatibilityChecker;

    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;
        configManager = new ConfigManager(this);

        commandManager =
                PaperCommandManager.builder()
                        .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                        .buildOnEnable(instance);

        arenaManager = new ArenaManager();
        kitManager = new KitManager();

        compatibilityChecker = new CompatibilityChecker(this);

        duelInviteManager = new DuelInviteManager();
        duelInviteManager.expireTimer();

        registerCommands();

        setupDupeWorld();

        setupSchemFolder();

        arenaManager.load(configManager.arenasRecord());

        this.getLogger()
                .info("Loaded in " + configManager.arenasRecord().arenas().size() + " arena(s)!");

        for (Map.Entry<String, ArenaRecord> entry :
                configManager.arenasRecord().arenas().entrySet()) {

            this.getLogger().info(" - " + entry.getKey());
        }

        kitManager.load(configManager.kitsRecord());

        this.getLogger().info("Loaded in " + configManager.kitsRecord().kits().size() + " kit(s)!");

        for (Map.Entry<String, KitRecord> entry : configManager.kitsRecord().kits().entrySet()) {

            this.getLogger().info(" - " + entry.getKey());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommands() {
        AnnotationParser<CommandSourceStack> parser =
                new AnnotationParser<>(commandManager, CommandSourceStack.class);

        parser.parse(new PingCommand(), new ArenaCommand(), new KitCommand(), new DuelCommand());
    }

    public void setupDupeWorld() {

        World world = Bukkit.getWorld("arenas");

        if (world == null) {

            WorldCreator creator = new WorldCreator("arenas");
            creator.generator(new DupeArenaGenerator());
            creator.environment(World.Environment.NORMAL);

            creator.generateStructures(false);

            world = Bukkit.createWorld(creator);
        }
    }

    public void setupSchemFolder() {

        Path dataPath = getDataPath();

        File schem = new File(dataPath.toFile(), "schem");

        if (!schem.exists()) {
            schem.mkdir();
        }
    }
}
