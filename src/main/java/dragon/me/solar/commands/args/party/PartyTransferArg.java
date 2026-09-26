package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyTransferArg {

    private final PartyCommandContext context;

    public PartyTransferArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party transfer <player>")
    public void transfer(CommandSourceStack stack, @Argument("player") Player player) {

        if (!(stack.getSender() instanceof Player p)) {

            context.sendConsoleError(stack);
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(p.getUniqueId());

        if (party == null) {

            context.send(p, context.language().notInParty());
            return;
        }

        if (!party.getOwner().equals(p.getUniqueId())) {

            context.send(p, context.language().cantDoPartyAsMemberOnly(), Placeholder.parsed("action", "transfer"));
            return;
        }

        context.partyManager().transferOwnership(party, player.getUniqueId());

        context.partyManager().executeForEachMember(party, m -> {
            context.send(m, context.language().partyTransfer(), Placeholder.parsed("player", player.getName()));
        });
    }
}
