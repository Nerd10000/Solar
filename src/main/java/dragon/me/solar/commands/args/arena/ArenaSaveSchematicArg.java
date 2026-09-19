package dragon.me.solar.commands.args.arena;

import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.hooks.Compatibilities;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class ArenaSaveSchematicArg {
    private final MessageService messages;

    public ArenaSaveSchematicArg(MessageService messages) {
        this.messages = messages;
    }

    @Command("arenas saveSchematic <name>")
    @Permission("solar.arena.manage.saveSchematic")
    public void saveSchematic(
            CommandSourceStack stack,
            @Argument(value = "name", suggestions = "arenas") String name) {
        if (!Solar.compatibilityChecker.isCompatibleWith(Compatibilities.FAWE)) {
            messages.send(
                    stack,
                    messages.language().missingDependency(),
                    Placeholder.parsed("dependency", "FastAsyncWorldedit (FAWE)"));
            return;
        }
        if (!(stack.getSender() instanceof Player player)) {
            sendConsoleError(stack);
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(name);
        if (arena == null) {
            messages.send(player, messages.language().arenaNotFound());
            return;
        }

        FaweHook.createArenaSchematic(arena, player.getWorld())
                .thenRun(
                        () ->
                                messages.send(
                                        player,
                                        messages.language().arenaSchemSaved(),
                                        Placeholder.parsed("name", name)))
                .exceptionally(
                        error -> {
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

    private void sendConsoleError(CommandSourceStack stack) {
        messages.sendConsoleError(stack);
    }
}
