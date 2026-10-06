package dragon.me.solar.commands.args.queues;

import dragon.me.solar.queue.InMemoryQueue;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class QueueLeaveArg {
    private final QueueCommandContext context;

    public QueueLeaveArg(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue leave <queue>")
    public void leave(CommandSourceStack stack, @Argument("queue") String queue) {

        if (!(stack.getSender() instanceof Player p)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryQueue inMemoryQueue = context.queueManager().get(queue);

        if (inMemoryQueue == null) {
            context.send(p, context.messages().language().queueNotExists(), Placeholder.parsed("queue", queue));
            return;
        }

        if (!inMemoryQueue.inQueue(p.getUniqueId())) {

            context.send(p, context.messages().language().notInQueue(), Placeholder.parsed("queue", queue));
            return;
        }

        context.queueService().leaveQueue(p.getUniqueId(), inMemoryQueue.name);

        context.send(p, context.messages().language().leftQueue(), Placeholder.parsed("queue", inMemoryQueue.name));
    }
}
