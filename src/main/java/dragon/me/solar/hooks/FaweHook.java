package dragon.me.solar.hooks;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.CompletableFuture;
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
}
