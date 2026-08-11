package dragon.me.solar.configs;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.ArenasRecord;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.configs.records.KitsRecord;
import dragon.me.solar.configs.records.LanguageRecord;
import dragon.me.solar.configs.records.LobbyRecord;
import dragon.me.solar.configs.records.SettingsRecord;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.ConfigurateException;

public class ConfigManager {

    private LanguageRecord languageRecord;
    private SettingsRecord settingsRecord;
    private ArenasRecord arenasRecord;
    private KitsRecord kitsRecord;

    private ConfigLoader<LanguageRecord> languageLoader;
    private ConfigLoader<ArenasRecord> arenaLoader;
    private ConfigLoader<KitsRecord> kitLoader;
    private ConfigLoader<SettingsRecord> settingsLoader;

    public ConfigManager(Solar solar) {

        Path folder = solar.getDataPath();

        try {
            languageLoader =
                    new ConfigLoader<>(folder.resolve("language.yml"), LanguageRecord.class);
            languageRecord = languageLoader.load(LanguageRecord.DEFAULTS);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        settingsLoader = new ConfigLoader<>(folder.resolve("config.yml"), SettingsRecord.class);

        try {
            settingsRecord = settingsLoader.load(SettingsRecord.DEFAULTS);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        try {
            arenaLoader = new ConfigLoader<>(folder.resolve("arenas.yml"), ArenasRecord.class);

            arenasRecord = arenaLoader.load(ArenasRecord.DEFAULTS);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        try {
            kitLoader = new ConfigLoader<>(folder.resolve("kits.yml"), KitsRecord.class);

            kitsRecord = kitLoader.load(KitsRecord.DEFAULTS);

        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadAll() {
        try {
            if (languageLoader != null) {
                languageRecord = languageLoader.load(LanguageRecord.DEFAULTS);
            }

            if (settingsLoader != null) {
                settingsRecord = settingsLoader.load(SettingsRecord.DEFAULTS);
            }

            if (arenaLoader != null) {
                arenasRecord = arenaLoader.load(ArenasRecord.DEFAULTS);
            }

            if (kitLoader != null) {
                kitsRecord = kitLoader.load(KitsRecord.DEFAULTS);
            }
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public LanguageRecord languageRecord() {
        return languageRecord;
    }

    public SettingsRecord settingsRecord() {
        return settingsRecord;
    }

    public ArenasRecord arenasRecord() {
        return arenasRecord;
    }

    public KitsRecord kitsRecord() {
        return kitsRecord;
    }

    public void registerArena(ArenaRecord record) {

        arenasRecord = arenasRecord.addArena(record);
        try {
            arenaLoader.save(arenasRecord);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerKit(KitRecord record) {
        kitsRecord = kitsRecord.addKit(record);
        try {
            kitLoader.save(kitsRecord);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLobbyLocation(LobbyRecord record) {
        settingsRecord = settingsRecord.updateLobbyLocation(record);
        try {
            settingsLoader.save(settingsRecord);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }

    public Location getLobbyLocation() {
        LobbyRecord lobby = settingsRecord.lobbyRecord();
        if (lobby == null || lobby.name() == null) {
            return null;
        }

        World world = Bukkit.getWorld(lobby.name());
        if (world == null) {
            return null;
        }

        return new Location(world, lobby.x(), lobby.y(), lobby.z(), lobby.yaw(), lobby.pitch());
    }
}
