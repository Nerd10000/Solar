package dragon.me.solar.commands.args.arena;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaSpawn;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class ArenaSpawnArg {
    private final MessageService messages;

    public ArenaSpawnArg(MessageService messages) {
        this.messages = messages;
    }

    @Command("arenas setSpawn <name> <number>")
    @Permission("solar.arena.manage.setspawn")
    public void setSpawn(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name,
            @Argument("number") @Range(min = "1", max = "2") int value) {
        if (!(stack.getSender() instanceof Player player)) {
            messages.sendConsoleError(stack);
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);
        if (arena == null) {
            messages.send(player, messages.language().arenaNotFound());
            return;
        }

        BlockVector3 origin = arena.getPasteOrigin();
        if (value == 1) {
            arena.spawn1 = ArenaSpawn.fromAbsolute(player.getLocation(), origin);
            arena.isSpawns1Set = true;
        } else {
            arena.spawn2 = ArenaSpawn.fromAbsolute(player.getLocation(), origin);
            arena.isSpawn2Set = true;
        }

        messages.send(
                player,
                messages.language().arenaSpawnSet(),
                Placeholder.parsed("spawn", String.valueOf(value)),
                Placeholder.parsed("arena", arena.name));
    }
}
