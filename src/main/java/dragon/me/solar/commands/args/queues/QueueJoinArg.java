package dragon.me.solar.commands.args.queues;

import dragon.me.solar.Solar;
import dragon.me.solar.queue.InMemoryQueue;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class QueueJoinArg {

    private final QueueCommandContext context;

    public QueueJoinArg(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue join <queue>")
    public void join(CommandSourceStack stack, @Argument("queue") String queue) {

        if (!(stack.getSender() instanceof Player p)) {
            context.sendConsoleError(stack);
            return;
        }

        if (Solar.MAINTENANCE_MODE) {
            context.send(p, context.messages().language().maintenancePrevention());
            return;
        }

        InMemoryQueue inMemoryQueue = context.queueManager().get(queue);

        if (inMemoryQueue == null) {

            context.send(p, context.messages().language().queueNotExists(), Placeholder.parsed("queue", queue));
            return;
        } else if (!inMemoryQueue.isEnabled) {
            context.send(p, context.messages().language().queueNotExists(), Placeholder.parsed("queue", queue));
            return; // TODO: Make a new message for it!
        }

        context.send(p, context.messages().language().joinedQueue(), Placeholder.parsed("queue", queue));

        // TODO: Add queue service logic here!
        context.queueService().joinQueue(p.getUniqueId(), inMemoryQueue.name);
    }
}
