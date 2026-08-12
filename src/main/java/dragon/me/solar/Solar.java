package dragon.me.solar;

import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.DupeArenaGenerator;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.commands.*;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.hooks.CompatibilityChecker;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.listeners.*;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.utils.MatchEndReason;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
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
    public static MatchManager matchManager;
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static CompatibilityChecker compatibilityChecker;
    public static GridManager gridManager = new GridManager();

    public static boolean MAINTENANCE_MODE = false;

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
        matchManager = new MatchManager();
        registerCommands();
        registerListeners();

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
        List<InMemoryMatch> matches = matchManager.getMatchList();

        if (!matches.isEmpty()) {
            getLogger()
                    .warning(
                            "The server is shutting down while "
                                    + matches.size()
                                    + " match(es) are active.");
            getLogger().warning("Active matches will be terminated.");

            getLogger()
                    .warning(
                            "For planned maintenance, use '/solar maintenance' to prevent new"
                                    + " matches from starting.");
            getLogger().warning("Allow ongoing matches to finish before restarting the server.");
            getLogger().warning("Maintenance mode is disabled by default after a server restart.");
        }

        for (InMemoryMatch match : matches) {
            matchManager.endMatchInstant(match, null, MatchEndReason.TERMINATED);
        }
    }

    public void registerCommands() {
        AnnotationParser<CommandSourceStack> parser =
                new AnnotationParser<>(commandManager, CommandSourceStack.class);

        parser.parse(
                new PingCommand(),
                new ArenaCommand(),
                new KitCommand(),
                new DuelCommand(),
                new SolarCommand(),
                new SpectateCommand());
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerItemUseListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakAndPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public void setupDupeWorld() {

        World world = Bukkit.getWorld("arenas");

        if (world == null) {

            WorldCreator creator = new WorldCreator("arenas");
            creator.generator(new DupeArenaGenerator());
            creator.environment(World.Environment.NORMAL);

            creator.generateStructures(false);

            world = Bukkit.createWorld(creator);

            // This is useless as almost everytime the world will exist.
            if (world == null) return;

            world.setGameRule(GameRules.ADVANCE_TIME, false);
            world.setGameRule(GameRules.ADVANCE_WEATHER, false);
            world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);

            world.setGameRule(GameRules.LOCATOR_BAR, false);
            world.setGameRule(GameRules.SPAWN_MOBS, false);
            world.setGameRule(GameRules.SPAWN_MONSTERS, false);
            world.setGameRule(GameRules.SPAWN_PATROLS, false);
            world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
            world.setGameRule(GameRules.SPAWN_WARDENS, false);
            world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
            world.setGameRule(GameRules.SPAWNER_BLOCKS_WORK, false);
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
