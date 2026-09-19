package dragon.me.solar.commands.args.arena;

import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class ArenaCreateArg {
    private final MessageService messages;

    public ArenaCreateArg(MessageService messages) {
        this.messages = messages;
    }

    @Command("arenas create <name>")
    @Permission("solar.arena.manage.create")
    public void create(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name) {
        if (!(stack.getSender() instanceof Player player)) {
            messages.sendConsoleError(stack);
            return;
        }

        InMemoryArena arena = new InMemoryArena(name);
        try {
            Solar.arenaManager.create(arena);
        } catch (RuntimeException exception) {
            messages.send(player, messages.language().arenaAlreadyExists());
            return;
        }

        messages.send(
                player,
                messages.language().arenaCreated(),
                Placeholder.parsed("arena", arena.name));
    }
}
