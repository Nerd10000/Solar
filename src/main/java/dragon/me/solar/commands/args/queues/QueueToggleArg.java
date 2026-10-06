package dragon.me.solar.commands.args.queues;

import dragon.me.solar.queue.InMemoryQueue;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class QueueToggleArg {

    private final QueueCommandContext context;

    public QueueToggleArg(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue toggle <queue>")
    @Permission("solar.queue.toggle")
    public void toggle(CommandSourceStack stack, @Argument("queue") String queue) {

        if (!(stack.getSender() instanceof Player p)) {

            context.sendConsoleError(stack);
            return;
        }

        InMemoryQueue inMemoryQueue = context.queueManager().get(queue);

        if (queue == null) {

            context.send(p, context.messages().language().queueNotExists());
            return;
        }

        inMemoryQueue.isEnabled = !inMemoryQueue.isEnabled;

        context.send(
                p,
                context.messages().language().toggleQueue(),
                Placeholder.parsed("status", inMemoryQueue.isEnabled ? "ON" : "OFF"),
                Placeholder.parsed("queue", queue));
    }
}
