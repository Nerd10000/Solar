package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.invite.PartyInviteRecord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyDenyArg {

    private final PartyCommandContext context;

    public PartyDenyArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party deny <player>")
    public void deny(CommandSourceStack stack, @Argument("player") Player player) {

        if (!(stack.getSender() instanceof Player p)) {
            context.sendConsoleError(stack);
            return;
        }
        PartyInviteRecord invite = context.partyInviteManager().getInviteBySender(player.getUniqueId());
        if (invite == null) {

            context.send(p, context.language().notInviteFromParty());
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(player.getUniqueId());

        if (party == null) {

            context.send(p, context.language().noSuchParty());
            context.partyInviteManager().removeInvite(invite);
            return;
        }

        context.send(p, context.language().partyInviteDenied(), Placeholder.parsed("player", player.getName()));

        context.partyInviteManager().removeInvite(invite);
    }
}
