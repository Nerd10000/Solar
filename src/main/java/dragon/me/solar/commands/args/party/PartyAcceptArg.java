package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.invite.PartyInviteRecord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyAcceptArg {
    private final PartyCommandContext context;

    public PartyAcceptArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party accept <player>")
    public void accept(CommandSourceStack stack, @Argument("player") Player inviter) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }
        if (context.partyManager().isMemberOfAParty(player.getUniqueId())) {
            context.send(player, context.language().cantCreatePartyAsMemberOfAParty());
            return;
        }

        PartyInviteRecord invite =
                context.partyInviteManager().getInviteBySender(inviter.getUniqueId());
        if (invite == null) {
            context.send(player, context.language().notInviteFromParty());
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(inviter.getUniqueId());
        if (party == null) {
            context.send(player, context.language().noSuchParty());
            context.partyInviteManager().removeInvite(invite);
            return;
        }

        party.getMemberList().add(player.getUniqueId());
        context.partyInviteManager().removeInvite(invite);
        context.partyManager()
                .executeForEachMember(
                        party,
                        member ->
                                context.send(
                                        member,
                                        context.language().playerJoinedParty(),
                                        Placeholder.parsed("player", player.getName())));
    }
}
