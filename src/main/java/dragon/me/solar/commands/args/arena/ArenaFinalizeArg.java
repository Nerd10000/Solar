package dragon.me.solar.commands.args.arena;

import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.configs.records.ArenaRecord;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class ArenaFinalizeArg {
    private final MessageService messages;

    public ArenaFinalizeArg(MessageService messages) {
        this.messages = messages;
    }

    @Command("arenas finalize <name>")
    @Permission("solar.arena.manage.finalize")
    public void finalizeArena(CommandSourceStack stack, @Argument(value = "name", suggestions = "arenas") String name) {
        if (!(stack.getSender() instanceof Player player)) {
            messages.sendConsoleError(stack);
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);
        if (arena == null) {
            messages.send(player, messages.language().arenaNotFound());
            return;
        }
        if (!arena.canBeFinalized()) {
            messages.send(player, messages.language().arenaCantBeFinalized());
            return;
        }

        ArenaRecord record = arena.toRecord();
        Solar.configManager.registerArena(record);
        messages.send(player, messages.language().arenaFinalized());
    }
}
