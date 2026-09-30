package dragon.me.solar.commands;

import dragon.me.solar.commands.args.queues.QueueCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import org.incendo.cloud.annotations.Command;

public class QueueCommand {

    private final QueueCommandContext context;

    public QueueCommand(QueueCommandContext context) {
        this.context = context;
    }

    @Command("queue")
    public void mainCommand(CommandSourceStack stack) {

        List<String> unparsed = context.configManager().languageRecord().partyHelp();

        for (String s : unparsed) {}
    }
}
