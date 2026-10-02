package dragon.me.solar.commands.args.queues;

import dragon.me.solar.queue.InMemoryQueue;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class QueueCreateArg {

    private final QueueCommandContext context;

    public QueueCreateArg(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue create <name> <kit>")
    @Permission("solar.queue.create")
    public void create(
            CommandSourceStack stack,
            @Argument("name") String name,
            @Argument(value = "kit", suggestions = "kits") String kit) {

        if (!(stack.getSender() instanceof Player p)) {

            context.sendConsoleError(stack);
            return;
        }

        // Check the kit if it exists!

        if (context.kitService().getKit(name) == null) {

            context.send(p, context.messages().language().kitNotFound(), Placeholder.parsed("kit", kit));
            return;
        }

        // Create the Queue in memory

        InMemoryQueue queue = new InMemoryQueue(name, kit);

        boolean result = context.queueManager().create(queue);

        if (!result) {
            context.send(p, context.messages().language().queueExists());
            return;
        }

        context.send(p, context.messages().language().queueCreated(), Placeholder.parsed("queue", queue.name));
    }
}
