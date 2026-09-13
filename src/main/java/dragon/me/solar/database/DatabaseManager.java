package dragon.me.solar.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dragon.me.solar.Solar;
import dragon.me.solar.database.models.PlayerStat;
import dragon.me.solar.database.models.PlayerStore;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.Nullable;

public class DatabaseManager {

    private String url;
    private ConnectionSource source;
    private Dao<PlayerStat, String> statsDao;
    private Dao<PlayerStore, String> storeDao;

    private final ExecutorService thread = Executors.newVirtualThreadPerTaskExecutor();

    public DatabaseManager(String filename) {

        this.url = "jdbc:sqlite:" + Solar.instance.getDataPath() + "/" + filename;

        try {
            this.source = new JdbcConnectionSource(this.url);
            this.statsDao = DaoManager.createDao(source, PlayerStat.class);
            //            this.storeDao = DaoManager.createDao(source, PlayerStore.class);

            TableUtils.createTableIfNotExists(source, PlayerStat.class);
            //            TableUtils.createTableIfNotExists(source, PlayerStore.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @Nullable CompletableFuture<PlayerStat> getStatById(UUID uuid, String kit) {

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return statsDao.queryBuilder()
                                .where()
                                .eq("uuid", uuid.toString())
                                .and()
                                .eq("kit", kit)
                                .queryForFirst();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                thread);
    }

    public CompletableFuture<List<PlayerStat>> getStatsById(UUID uuid) {

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return statsDao.queryBuilder().where().eq("uuid", uuid.toString()).query();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                thread);
    }

    public CompletableFuture<Void> updateStats(PlayerStat stat) {

        return CompletableFuture.supplyAsync(
                () -> {
                    try {

                        statsDao.createOrUpdate(stat).isCreated();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                },
                thread);
    }

    public CompletableFuture<Void> updateStore(PlayerStore store) {

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        storeDao.createOrUpdate(store);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    return null;
                },
                thread);
    }

    public CompletableFuture<PlayerStore> getStoreById(UUID uuid) {

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return storeDao.queryBuilder()
                                .where()
                                .eq("uuid", uuid.toString())
                                .queryForFirst();

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                },
                thread);
    }

    public CompletableFuture<Void> removeStoreById(UUID uuid) {

        return CompletableFuture.supplyAsync(
                () -> {
                    getStoreById(uuid)
                            .thenAccept(
                                    store -> {
                                        try {
                                            storeDao.delete(store);
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                    return null;
                },
                thread);
    }
}
