package dragon.me.solar.commands.args.queues;

import dragon.me.solar.configs.records.QueueFlagsRecord;
import dragon.me.solar.queue.InMemoryQueue;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;

public class QueueSetFlags {

    private final QueueCommandContext context;

    public QueueSetFlags(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue set-flags <name>")
    public void setFlags(
            CommandSourceStack stack,
            @Argument("name") String name,
            @Flag("weight") @Default(value = "None") QueueType weight,
            @Flag("team-size") @Default(value = "1") Integer teamSize,
            @Flag("allow-parties") @Default(value = "false") Boolean allowParties,
            @Flag("min-rating") @Default("0") Integer minRating,
            @Flag("permission") @Default("") String permission,
            @Flag("min-party-size") @Default("0") Integer minPartySize,
            @Flag("max-party-size") @Default("-1") Integer maxPartySize) {

        if (!(stack.getSender() instanceof Player p)) {

            context.sendConsoleError(stack);
            return;
        }

        InMemoryQueue queue = context.queueManager().get(name);

        if (queue == null) {

            context.send(p, context.messages().language().queueNotExists());
            return;
        }

        queue.flags = new QueueFlagsRecord(
                weight != null ? weight.name() : "None",
                teamSize != 0 ? teamSize : 1,
                allowParties != null && allowParties,
                minRating != null ? minRating : 0,
                permission != null ? permission : "",
                minPartySize != null ? minPartySize : 1,
                maxPartySize != null ? maxPartySize : 1);

        context.send(p, context.messages().language().queueFlagsSet(), Placeholder.parsed("queue", queue.name));
    }
}
