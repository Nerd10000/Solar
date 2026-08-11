package dragon.me.solar.hooks;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.configs.records.ArenaRecord;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class FaweHook {

    public static CompletableFuture<Void> createArenaSchematic(InMemoryArena arena, World world) {

        return CompletableFuture.runAsync(
                () -> {
                    BlockVector3 pos1 = BlockVector3.at(arena.minX, arena.minY, arena.minZ);

                    BlockVector3 pos2 = BlockVector3.at(arena.maxX, arena.maxY, arena.maxZ);

                    com.sk89q.worldedit.world.World worldeditWorld = BukkitAdapter.adapt(world);

                    CuboidRegion region = new CuboidRegion(worldeditWorld, pos1, pos2);

                    BlockVector3 center =
                            BlockVector3.at(
                                    (arena.minX + arena.maxX) / 2,
                                    arena.minY,
                                    (arena.minZ + arena.maxZ) / 2);

                    try (EditSession editSession =
                            WorldEdit.getInstance()
                                    .newEditSessionBuilder()
                                    .world(worldeditWorld)
                                    .fastMode(true)
                                    .build()) {

                        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

                        // IMPORTANT: keep clipboard coordinates consistent
                        clipboard.setOrigin(center);

                        ForwardExtentCopy copy =
                                new ForwardExtentCopy(
                                        editSession, region, clipboard, region.getMinimumPoint());

                        copy.setCopyingEntities(true);
                        copy.setCopyingBiomes(false);

                        Operations.complete(copy);

                        File file =
                                new File(
                                        Solar.instance.getDataFolder(),
                                        "schem/" + arena.name + ".schem");

                        file.getParentFile().mkdirs();

                        ClipboardFormat format = ClipboardFormats.findByAlias("sponge.3");

                        if (format == null) {
                            throw new IllegalStateException("Sponge schematic format not found");
                        }

                        try (ClipboardWriter writer =
                                format.getWriter(new FileOutputStream(file))) {

                            writer.write(clipboard);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public static CompletableFuture<Boolean> pasteArena(String arenaName, BlockVector3 center) {

        return CompletableFuture.supplyAsync(
                () -> {
                    ArenaRecord arena = Solar.configManager.arenasRecord().arenas().get(arenaName);

                    if (arena == null) {
                        Solar.instance
                                .getLogger()
                                .warning("Failed to paste arena: no such arena: " + arenaName);
                        return false;
                    }

                    File schematicFile =
                            new File(Solar.instance.getDataFolder(), arena.schematicPath());

                    ClipboardFormat format = ClipboardFormats.findByAlias("sponge.3");

                    if (format == null) {
                        Solar.instance
                                .getLogger()
                                .warning("Could not find Sponge schematic format.");
                        return false;
                    }

                    try (ClipboardReader reader =
                            format.getReader(new FileInputStream(schematicFile))) {

                        Clipboard clipboard = reader.read();

                        try (EditSession editSession =
                                WorldEdit.getInstance()
                                        .newEditSession(
                                                new BukkitWorld(Bukkit.getWorld("arenas")))) {

                            Operation operation =
                                    new ClipboardHolder(clipboard)
                                            .createPaste(editSession)
                                            .to(center)
                                            .ignoreAirBlocks(true)
                                            .copyEntities(false)
                                            .build();

                            Operations.complete(operation);
                        }

                        return true;

                    } catch (IOException | WorldEditException e) {
                        Solar.instance
                                .getLogger()
                                .log(Level.SEVERE, "Failed to paste arena " + arenaName, e);
                        return false;
                    }
                });
    }

    public static CompletableFuture<Void> clearArena(String arenaName, BlockVector3 center) {
        return CompletableFuture.runAsync(
                () -> {
                    ArenaRecord arena = Solar.configManager.arenasRecord().arenas().get(arenaName);
                    if (arena == null) {
                        return;
                    }

                    int originX = (arena.minX() + arena.maxX()) / 2;
                    int originZ = (arena.minZ() + arena.maxZ()) / 2;

                    BlockVector3 min =
                            BlockVector3.at(
                                    center.x() + arena.minX() - originX,
                                    center.y() + arena.minY() - arena.minY(),
                                    center.z() + arena.minZ() - originZ);
                    BlockVector3 max =
                            BlockVector3.at(
                                    center.x() + arena.maxX() - originX,
                                    center.y() + arena.maxY() - arena.minY(),
                                    center.z() + arena.maxZ() - originZ);

                    com.sk89q.worldedit.world.World worldeditWorld =
                            BukkitAdapter.adapt(Bukkit.getWorld("arenas"));
                    CuboidRegion region = new CuboidRegion(worldeditWorld, min, max);

                    try (EditSession editSession =
                            WorldEdit.getInstance()
                                    .newEditSessionBuilder()
                                    .world(worldeditWorld)
                                    .fastMode(true)
                                    .build()) {
                        editSession.setBlocks(
                                (com.sk89q.worldedit.regions.Region) region,
                                (com.sk89q.worldedit.world.block.BlockState)
                                        BlockTypes.AIR.getDefaultState());
                    } catch (WorldEditException e) {
                        Solar.instance
                                .getLogger()
                                .log(Level.SEVERE, "Failed to clear arena " + arenaName, e);
                    }
                });
    }

    public static void clearArenaSync(String arenaName, BlockVector3 center) {
        ArenaRecord arena = Solar.configManager.arenasRecord().arenas().get(arenaName);
        if (arena == null) {
            return;
        }

        int originX = (arena.minX() + arena.maxX()) / 2;
        int originZ = (arena.minZ() + arena.maxZ()) / 2;

        BlockVector3 min =
                BlockVector3.at(
                        center.x() + arena.minX() - originX,
                        center.y() + arena.minY() - arena.minY(),
                        center.z() + arena.minZ() - originZ);
        BlockVector3 max =
                BlockVector3.at(
                        center.x() + arena.maxX() - originX,
                        center.y() + arena.maxY() - arena.minY(),
                        center.z() + arena.maxZ() - originZ);

        org.bukkit.World bukkitWorld = Bukkit.getWorld("arenas");
        if (bukkitWorld == null) return;

        com.sk89q.worldedit.world.World worldeditWorld = BukkitAdapter.adapt(bukkitWorld);
        CuboidRegion region = new CuboidRegion(worldeditWorld, min, max);

        // Synchronous EditSession without FastMode so changes commit immediately to Bukkit
        try (EditSession editSession =
                WorldEdit.getInstance()
                        .newEditSessionBuilder()
                        .world(worldeditWorld)
                        .fastMode(false)
                        .build()) {

            editSession.setBlocks(
                    (com.sk89q.worldedit.regions.Region) region, BlockTypes.AIR.getDefaultState());
            editSession.flushQueue(); // Ensure queued operations are explicitly flushed
        } catch (WorldEditException e) {
            Solar.instance
                    .getLogger()
                    .log(Level.SEVERE, "Failed to synchronously clear arena " + arenaName, e);
        }
    }
}
