package dragon.me.solar.commands.args.arena;

import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class ArenaEdgeArg {
    private final MessageService messages;

    public ArenaEdgeArg(MessageService messages) {
        this.messages = messages;
    }

    @Command("arenas setEdge <name> <number>")
    @Permission("solar.arena.manage.setedge")
    public void setEdge(
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

        if (value == 1) {
            arena.minX = player.getLocation().getBlockX();
            arena.minY = player.getLocation().getBlockY();
            arena.minZ = player.getLocation().getBlockZ();
            arena.isMinBorderSet = true;
        } else {
            arena.maxX = player.getLocation().getBlockX();
            arena.maxY = player.getLocation().getBlockY();
            arena.maxZ = player.getLocation().getBlockZ();
            arena.isMaxBorderSet = true;
        }

        messages.send(
                player,
                messages.language().arenaEdgeSet(),
                Placeholder.parsed("edge", String.valueOf(value)),
                Placeholder.parsed("arena", arena.name));
    }
}
