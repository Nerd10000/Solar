package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyKickArg {

    private final PartyCommandContext context;

    public PartyKickArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party kick <player>")
    public void kick(CommandSourceStack stack, @Argument("player") Player player) {

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

            context.send(p, context.language().notPartyLeader());
            return;
        }

        if (!context.partyManager().getPartyByMember(player.getUniqueId()).equals(party)) {

            context.send(p, context.language().notInParty());
            return;
        }

        context.partyManager().executeForEachMember(party, m -> {
            context.send(m, context.language().partyKickPlayer(), Placeholder.parsed("player", player.getName()));
        });

        party.getMemberList().remove(player.getUniqueId());
    }
}
