package dragon.me.solar.commands.args.queues;

import dragon.me.solar.queue.InMemoryQueue;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class QueueFinalizeArg {

    private final QueueCommandContext context;

    public QueueFinalizeArg(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue finalize <name>")
    @Permission("solar.queue.finalize")
    public void finalize(CommandSourceStack stack, @Argument("name") String name) {

        if (!(stack.getSender() instanceof Player p)) {

            context.sendConsoleError(stack);
            return;
        }

        InMemoryQueue queue = context.queueManager().get(name);

        if (queue == null) {
            context.send(p, context.messages().language().queueNotExists(), Placeholder.parsed("queue", name));
            return;
        }

        context.configManager().registerQueue(queue.toRecord());

        context.send(p, context.messages().language().queueFlagsSet(), Placeholder.parsed("queue", queue.name));
    }
}
