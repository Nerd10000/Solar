package dragon.me.solar;

import dragon.me.solar.api.SolarAPI;
import dragon.me.solar.application.AntiPiracy;
import dragon.me.solar.application.SolarContext;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.DupeArenaGenerator;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.commands.*;
import dragon.me.solar.commands.args.arena.ArenaCreateArg;
import dragon.me.solar.commands.args.arena.ArenaEdgeArg;
import dragon.me.solar.commands.args.arena.ArenaFinalizeArg;
import dragon.me.solar.commands.args.arena.ArenaSaveSchematicArg;
import dragon.me.solar.commands.args.arena.ArenaSpawnArg;
import dragon.me.solar.commands.args.duel.DuelAcceptArg;
import dragon.me.solar.commands.args.duel.DuelDeclineArg;
import dragon.me.solar.commands.args.duel.DuelRequestArg;
import dragon.me.solar.commands.args.kit.KitCommandContext;
import dragon.me.solar.commands.args.kit.KitCreateArg;
import dragon.me.solar.commands.args.kit.KitFinalizeArg;
import dragon.me.solar.commands.args.kit.KitGiveArg;
import dragon.me.solar.commands.args.kit.KitSetEffectsArg;
import dragon.me.solar.commands.args.kit.KitSetFlagsArg;
import dragon.me.solar.commands.args.kit.KitSetItemsArg;
import dragon.me.solar.commands.args.party.*;
import dragon.me.solar.commands.args.queues.*;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.configs.records.QueueRecord;
import dragon.me.solar.database.DatabaseManager;
import dragon.me.solar.database.PlayerCache;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.hooks.Compatibilities;
import dragon.me.solar.hooks.CompatibilityChecker;
import dragon.me.solar.hooks.api.SolarApiImpl;
import dragon.me.solar.hooks.intave.IntavePunishmentListener;
import dragon.me.solar.hooks.papi.SolarExpansion;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.kit.KitService;
import dragon.me.solar.listeners.*;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.party.PartyManager;
import dragon.me.solar.party.PartyService;
import dragon.me.solar.party.invite.PartyInviteManager;
import dragon.me.solar.queue.QueueActionbarService;
import dragon.me.solar.queue.QueueManager;
import dragon.me.solar.queue.QueueService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public final class Solar extends JavaPlugin {

    public static Solar instance;
    private static SolarContext context;
    private static @NonNull PaperCommandManager<CommandSourceStack> commandManager;
    public static ConfigManager configManager;
    public static ArenaManager arenaManager;
    public static KitManager kitManager;
    public static KitService kitService;
    public static DuelInviteManager duelInviteManager;
    public static MatchManager matchManager;
    public static final MiniMessage miniMessage = MiniMessage.miniMessage();
    public static MessageService messageService;
    public static CompatibilityChecker compatibilityChecker;
    public static GridManager gridManager;
    public static PartyInviteManager partyInviteManager;
    public static PartyManager partyManager;
    public static PartyService partyService;
    public static DatabaseManager databaseManager;
    public static PlayerCache cache;
    public static QueueManager queueManager;

    public static MatchService matchService;
    public static boolean MAINTENANCE_MODE = false;

    public static QueueActionbarService queueActionbarService;
    public static QueueService queueService;

    public static SolarAPI solarAPI;

    public static SolarContext context() {
        return context;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        instance = this;
        context = SolarContext.create(this);

        AntiPiracy.check(true);

        configManager = context.configManager;
        arenaManager = context.arenaManager;
        gridManager = context.gridManager;
        kitManager = context.kitManager;
        kitService = context.kitService;
        duelInviteManager = context.duelInviteManager;
        matchManager = context.matchManager;
        messageService = context.messageService;
        compatibilityChecker = context.compatibilityChecker;
        partyInviteManager = context.partyInviteManager;
        partyManager = context.partyManager;
        partyService = context.partyService;
        databaseManager = context.databaseManager;
        cache = context.cache;
        matchService = context.matchService;

        commandManager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(instance);

        duelInviteManager.expireTimer();
        partyInviteManager.expireTimer();
        queueManager = new QueueManager(); // TODO: Move it to context
        queueService = new QueueService(queueManager, matchService); // Todo: Move it to context

        queueActionbarService = new QueueActionbarService(queueService, configManager, messageService);

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

        for (Map.Entry<String, KitRecord> entry :
                configManager.kitsRecord().kits().entrySet()) {
            this.getLogger().info(" - " + entry.getKey());
        }
        queueManager.load(configManager.queuesRecord());

        this.getLogger()
                .info("Loaded in " + configManager.queuesRecord().queues().size() + " queue(s)!");
        for (Map.Entry<String, QueueRecord> entry :
                configManager.queuesRecord().queues().entrySet()) {
            this.getLogger().info(" - " + entry.getKey());
        }

        registerCommands();
        registerListeners();

        if (compatibilityChecker.isCompatibleWith(Compatibilities.PAPI)) {

            new SolarExpansion(this).register();
        }

        queueActionbarService.tick();

        solarAPI = new SolarApiImpl();

        Bukkit.getServicesManager().register(SolarAPI.class, solarAPI, this, ServicePriority.Normal);
    }

    @Override
    public void onDisable() {
        List<InMemoryMatch> matches = matchManager.getMatchList();

        if (!matches.isEmpty()) {
            getLogger().warning("The server is shutting down while " + matches.size() + " match(es) are active.");
            getLogger().warning("Active matches will be terminated.");

            getLogger()
                    .warning("For planned maintenance, use '/solar maintenance' to prevent new"
                            + " matches from starting.");
            getLogger().warning("Allow ongoing matches to finish before restarting the server.");
            getLogger().warning("Maintenance mode is disabled by default after a server restart.");
        }

        for (InMemoryMatch match : matches) {
            matchService.terminate(match);
        }

        cleanup();
    }

    public void registerCommands() {
        AnnotationParser<CommandSourceStack> parser = new AnnotationParser<>(commandManager, CommandSourceStack.class);
        PartyCommandContext partyCommandContext =
                new PartyCommandContext(partyManager, partyInviteManager, configManager, miniMessage, messageService);
        KitCommandContext kitCommandContext =
                new KitCommandContext(kitManager, kitService, configManager, messageService);

        QueueCommandContext queueCommandContext =
                new QueueCommandContext(queueManager, kitService, configManager, messageService, queueService);

        parser.parse(
                new PingCommand(),
                new ArenaCommand(arenaManager, configManager, miniMessage),
                new ArenaCreateArg(messageService),
                new ArenaEdgeArg(messageService),
                new ArenaSpawnArg(messageService),
                new ArenaFinalizeArg(messageService),
                new ArenaSaveSchematicArg(messageService),
                new KitCommand(kitManager),
                new KitCreateArg(kitCommandContext),
                new KitSetItemsArg(kitCommandContext),
                new KitSetEffectsArg(kitCommandContext),
                new KitFinalizeArg(kitCommandContext),
                new KitGiveArg(kitCommandContext),
                new KitSetFlagsArg(kitCommandContext),
                new DuelCommand(arenaManager, kitManager),
                new DuelRequestArg(kitManager, duelInviteManager, messageService),
                new DuelAcceptArg(instance, arenaManager, gridManager, duelInviteManager, matchService, messageService),
                new DuelDeclineArg(duelInviteManager, messageService),
                new SolarCommand(configManager, miniMessage),
                new SpectateCommand(),
                new PartyCommand(configManager, miniMessage),
                new PartyCreateArg(partyCommandContext),
                new PartyLeaveArg(partyCommandContext),
                new PartyDisbandArg(partyCommandContext),
                new PartyInviteArg(partyCommandContext),
                new PartyAcceptArg(partyCommandContext),
                new PartyTransferArg(partyCommandContext),
                new PartyDenyArg(partyCommandContext),
                new PartyInfoArg(partyCommandContext),
                new PartyKickArg(partyCommandContext),
                new PartyBroadcastArg(partyCommandContext),
                new PartyStartArg(
                        partyManager,
                        partyService,
                        kitManager,
                        arenaManager,
                        gridManager,
                        matchService,
                        messageService),
                new QueueCommand(queueCommandContext),
                new QueueCreateArg(queueCommandContext),
                new QueueSetFlags(queueCommandContext),
                new QueueFinalizeArg(queueCommandContext),
                new QueueJoinArg(queueCommandContext),
                new QueueLeaveArg(queueCommandContext),
                new QueueToggleArg(queueCommandContext));
    }

    public void registerListeners() {
        getServer()
                .getPluginManager()
                .registerEvents(new PlayerDeathEventListener(matchService, matchManager, miniMessage), this);
        getServer()
                .getPluginManager()
                .registerEvents(
                        new PlayerQuitEventListener(matchManager, matchService, partyManager, messageService), this);
        /// getServer().getPluginManager().registerEvents(new PlayerItemUseListener(matchManager, kitManager), this);
        getServer().getPluginManager().registerEvents(new BlockBreakAndPlaceListener(matchManager, kitManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPickupItemListener(matchManager), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemListener(matchManager, kitManager), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(matchManager, kitManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(configManager, databaseManager), this);
        getServer()
                .getPluginManager()
                .registerEvents(new PlayerCommandSendEventListener(configManager, messageService), this);

        if (compatibilityChecker.isCompatibleWith(Compatibilities.INTAVE)) {
            getServer().getPluginManager().registerEvents(new IntavePunishmentListener(), this);
        }
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

    public void cleanup() {
        World world = Bukkit.getWorld("arenas");
        if (world == null) return;

        File folder = world.getWorldFolder();

        // Save + unload
        Bukkit.unloadWorld(world, false);

        if (deleteRecursively(folder)) {
            getLogger().info("World 'arenas' deleted successfully.");
        } else {
            getLogger().severe("Failed to delete world 'arenas'.");
        }
    }

    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}
