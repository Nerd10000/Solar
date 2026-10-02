package dragon.me.solar.application;

import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.database.DatabaseManager;
import dragon.me.solar.database.PlayerCache;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.hooks.CompatibilityChecker;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.kit.KitService;
import dragon.me.solar.match.MatchAnnouncementService;
import dragon.me.solar.match.MatchCountdownService;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.MatchPlayerStateService;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.party.PartyManager;
import dragon.me.solar.party.PartyService;
import dragon.me.solar.party.invite.PartyInviteManager;

public final class SolarContext {
    public final ConfigManager configManager;
    public final ArenaManager arenaManager;
    public final GridManager gridManager;
    public final KitManager kitManager;
    public final KitService kitService;
    public final DuelInviteManager duelInviteManager;
    public final MatchManager matchManager;
    public final MessageService messageService;
    public final CompatibilityChecker compatibilityChecker;
    public final PartyInviteManager partyInviteManager;
    public final PartyManager partyManager;
    public final PartyService partyService;
    public final DatabaseManager databaseManager;
    public final PlayerCache cache;
    public final MatchService matchService;

    private SolarContext(
            ConfigManager configManager,
            ArenaManager arenaManager,
            GridManager gridManager,
            KitManager kitManager,
            KitService kitService,
            DuelInviteManager duelInviteManager,
            MatchManager matchManager,
            MessageService messageService,
            CompatibilityChecker compatibilityChecker,
            PartyInviteManager partyInviteManager,
            PartyManager partyManager,
            PartyService partyService,
            DatabaseManager databaseManager,
            PlayerCache cache,
            MatchService matchService) {
        this.configManager = configManager;
        this.arenaManager = arenaManager;
        this.gridManager = gridManager;
        this.kitManager = kitManager;
        this.kitService = kitService;
        this.duelInviteManager = duelInviteManager;
        this.matchManager = matchManager;
        this.messageService = messageService;
        this.compatibilityChecker = compatibilityChecker;
        this.partyInviteManager = partyInviteManager;
        this.partyManager = partyManager;
        this.partyService = partyService;
        this.databaseManager = databaseManager;
        this.cache = cache;
        this.matchService = matchService;
    }

    public static SolarContext create(Solar plugin) {
        ConfigManager configManager = new ConfigManager(plugin);
        MessageService messageService = new MessageService(configManager, Solar.miniMessage);
        GridManager gridManager = new GridManager(configManager);
        ArenaManager arenaManager = new ArenaManager();
        KitManager kitManager = new KitManager();
        KitService kitService = new KitService(kitManager);
        DuelInviteManager duelInviteManager = new DuelInviteManager();
        MatchManager matchManager = new MatchManager();
        CompatibilityChecker compatibilityChecker = new CompatibilityChecker(plugin);
        PartyInviteManager partyInviteManager = new PartyInviteManager();
        PartyManager partyManager = new PartyManager();
        PartyService partyService = new PartyService(partyManager);
        DatabaseManager databaseManager = new DatabaseManager("solar.db");
        PlayerCache cache = new PlayerCache();
        MatchPlayerStateService playerStateService = new MatchPlayerStateService(plugin, configManager);
        MatchAnnouncementService announcementService = new MatchAnnouncementService(configManager, messageService);
        MatchCountdownService countdownService = new MatchCountdownService(plugin, configManager, messageService);
        MatchService matchService = new MatchService(
                matchManager,
                gridManager,
                configManager,
                kitManager,
                kitService,
                playerStateService,
                announcementService,
                countdownService,
                arenaManager);

        return new SolarContext(
                configManager,
                arenaManager,
                gridManager,
                kitManager,
                kitService,
                duelInviteManager,
                matchManager,
                messageService,
                compatibilityChecker,
                partyInviteManager,
                partyManager,
                partyService,
                databaseManager,
                cache,
                matchService);
    }
}
