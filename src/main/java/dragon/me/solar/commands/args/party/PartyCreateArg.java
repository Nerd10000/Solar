package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class PartyCreateArg {
    private final PartyCommandContext context;

    public PartyCreateArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party create")
    public void create(CommandSourceStack stack) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }
        if (context.partyManager().isMemberOfAParty(player.getUniqueId())) {
            context.send(player, context.language().cantCreatePartyAsMemberOfAParty());
            return;
        }

        context.partyManager().addParty(new InMemoryParty(UUID.randomUUID(), player.getUniqueId()));
        context.send(player, context.language().partyCreated());
    }
}
