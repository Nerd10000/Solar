package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.invite.PartyInviteRecord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyInviteArg {
    private final PartyCommandContext context;

    public PartyInviteArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party invite <player>")
    public void invite(CommandSourceStack stack, @Argument("player") Player target) {
        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(player.getUniqueId());
        if (party == null) {
            party = new InMemoryParty(UUID.randomUUID(), player.getUniqueId());
            context.partyManager().addParty(party);
        } else if (!party.getOwner().equals(player.getUniqueId())) {
            context.send(player, context.language().cantDoPartyAsMemberOnly(), Placeholder.parsed("action", "invite"));
            return;
        }
        if (context.partyManager().isMemberOfAParty(target.getUniqueId())) {
            return;
        }

        context.partyInviteManager()
                .addInvite(new PartyInviteRecord(
                        player.getUniqueId(), target.getUniqueId(), party.getUuid(), System.currentTimeMillis()));
        context.send(player, context.language().partyInviteSent(), Placeholder.parsed("player", target.getName()));
        context.send(target, context.language().partyInviteReceived(), Placeholder.parsed("player", player.getName()));
    }
}
