package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class PartyLeaveArg {

    private final PartyCommandContext context;

    public PartyLeaveArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party leave")
    public void leave(CommandSourceStack stack) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }
        if (!context.partyManager().isMemberOfAParty(player.getUniqueId())) {
            context.send(player, context.language().notInParty());
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(player.getUniqueId());
        if (party == null) {
            return;
        }
        if (party.getOwner().equals(player.getUniqueId())) {
            new PartyDisbandArg(context).disband(stack);
            return;
        }

        party.getMemberList().remove(player.getUniqueId());
        context.send(player, context.language().playerLeftParty());
        context.partyManager()
                .executeForEachMember(
                        party,
                        member -> context.send(
                                member,
                                context.language().playerLeftParty(),
                                Placeholder.parsed("player", player.getName())));
    }
}
