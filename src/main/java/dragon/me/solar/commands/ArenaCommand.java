package dragon.me.solar.commands;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaSpawn;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.hooks.Compatibilities;
import dragon.me.solar.hooks.FaweHook;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;

public class ArenaCommand {

    @Command("arenas create <name>")
    @Permission("solar.arena.manage.create")
    public void create(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryArena arena = new InMemoryArena(name);

        try {
            Solar.arenaManager.create(arena);
        } catch (RuntimeException e) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().arenaAlreadyExists(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().arenaCreated(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("arena", arena.name)));
    }

    @Command("arenas setEdge <name> <number>")
    @Permission("solar.arena.manage.setedge")
    public void setEdge(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name,
            @Argument("number") @Range(min = "1", max = "2") int value) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);

        if (arena == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().arenaNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        switch (value) {
            case 1: // LOWEST EDGE (PART)
                arena.minX = p.getLocation().getBlockX();
                arena.minY = p.getLocation().getBlockY();
                arena.minZ = p.getLocation().getBlockZ();

                arena.isMinBorderSet = true;

                break;

            case 2: // HIGHEST POINT / EDGE
                arena.maxX = p.getLocation().getBlockX();
                arena.maxY = p.getLocation().getBlockY();
                arena.maxZ = p.getLocation().getBlockZ();

                arena.isMaxBorderSet = true;

                break;
        }

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().arenaEdgeSet(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("edge", String.valueOf(value)),
                        Placeholder.parsed("arena", arena.name)));
    }

    @Command("arenas setSpawn <name> <number>")
    @Permission("solar.arena.manage.setspawn")
    public void setSpawn(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name,
            @Argument("number") @Range(min = "1", max = "2") int value) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);

        if (arena == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().arenaNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        BlockVector3 origin = arena.getPasteOrigin();

        switch (value) {
            case 1: // 1ST SPAWN POINT
                arena.spawn1 = ArenaSpawn.fromAbsolute(p.getLocation(), origin);
                arena.isSpawns1Set = true;

                break;

            case 2: // 2ND SPAWN POINT
                arena.spawn2 = ArenaSpawn.fromAbsolute(p.getLocation(), origin);
                arena.isSpawn2Set = true;

                break;
        }

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().arenaSpawnSet(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("spawn", String.valueOf(value)),
                        Placeholder.parsed("arena", arena.name)));
    }

    @Command("arenas finalize <name> ")
    @Permission("solar.arena.manage.finalize")
    public void finalize(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name) {

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);

        if (arena == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().arenaNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        if (!arena.canBeFinalized()) {

            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().arenaCantBeFinalized(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        ArenaRecord record = arena.toRecord();

        Solar.configManager.registerArena(record);

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().arenaFinalized(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));
    }

    @Command("arenas saveSchematic <name> ")
    @Permission("solar.arena.manage.saveSchematic")
    public void saveSchematic(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name) {

        if (!Solar.compatibilityChecker.isCompatibleWith(Compatibilities.FAWE)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().missingDependency(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("dependency", "FastAsyncWorldedit (FAWE)")));

            return;
        }

        if (!(stack.getSender() instanceof Player p)) {

            stack.getSender()
                    .sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().consoleCantRun(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);

        if (arena == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().arenaNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));

            return;
        }

        FaweHook.createArenaSchematic(arena, p.getWorld())
                .thenRun(
                        () -> {
                            p.sendMessage(
                                    Solar.miniMessage.deserialize(
                                            Solar.configManager.languageRecord().arenaSchemSaved(),
                                            Placeholder.parsed(
                                                    "prefix",
                                                    Solar.configManager.languageRecord().prefix()),
                                            Placeholder.parsed("name", name)));
                        })
                .exceptionally(
                        error -> {
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().arenaSchemError(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("name", name));

                            Solar.instance
                                    .getLogger()
                                    .warning(
                                            "An error happened while saving "
                                                    + name
                                                    + "'s schematic!");
                            error.printStackTrace();
                            return null;
                        });
    }

    @Suggestions("arenas")
    public List<String> arenaSuggestions() {

        return Solar.arenaManager.ARENAS.keySet().stream().toList();
    }
}
