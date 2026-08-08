package dragon.me.solar.configs;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.configs.records.ArenasRecord;
import dragon.me.solar.configs.records.KitRecord;
import dragon.me.solar.configs.records.KitsRecord;
import dragon.me.solar.configs.records.LanguageRecord;
import dragon.me.solar.configs.records.SettingsRecord;
import java.nio.file.Path;
import org.spongepowered.configurate.ConfigurateException;

public class ConfigManager {

    private final LanguageRecord languageRecord;
    private final SettingsRecord settingsRecord;
    private ArenasRecord arenasRecord;
    private KitsRecord kitsRecord;

    private ConfigLoader<ArenasRecord> arenaLoader;
    private ConfigLoader<KitsRecord> kitLoader;

    public ConfigManager(Solar solar) {

        Path folder = solar.getDataPath();

        try {
            languageRecord =
                    new ConfigLoader<>(folder.resolve("language.yml"), LanguageRecord.class)
                            .load(LanguageRecord.DEFAULTS);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }

        try {
            settingsRecord =
                    new ConfigLoader<>(folder.resolve("config.yml"), SettingsRecord.class)
                            .load(SettingsRecord.DEFAULTS);
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
}
