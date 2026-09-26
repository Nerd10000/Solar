package dragon.me.solar.commands.args.party;

import dragon.me.solar.Solar;
import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.invite.PartyInviteRecord;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.time.Instant;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class PartyBroadcastArg {

    private final PartyCommandContext context;

    public PartyBroadcastArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party broadcast")
    @Permission("solar.party.broadcast")
    public void broadcast(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(player.getUniqueId());

        if (party == null) {
            context.send(player, context.language().notInParty());
            return;
        }

        if (!party.getOwner().equals(player.getUniqueId())) {

            context.send(player, context.language().notPartyLeader());
            return;
        }

        broadcast(party, 20 * 30);
    }

    public void broadcast(InMemoryParty party, long intervalTicks) {
        Instant end = Instant.now().plusSeconds(60);

        Bukkit.getScheduler()
                .runTaskTimer(
                        Solar.instance,
                        task -> {
                            if (Instant.now().isAfter(end)) {
                                task.cancel();
                                return;
                            }

                            if (!context.partyManager().getParties().contains(party)) {
                                task.cancel();
                                return;
                            }

                            Player owner = Bukkit.getPlayer(party.getOwner());

                            if (owner == null) {
                                task.cancel();
                                return;
                            }

                            for (Player p : Bukkit.getOnlinePlayers()) {

                                if (party.getMemberList().contains(p.getUniqueId())) continue;

                                context.partyInviteManager()
                                        .addInvite(new PartyInviteRecord(
                                                party.getOwner(),
                                                p.getUniqueId(),
                                                party.getUuid(),
                                                System.currentTimeMillis()));

                                context.send(
                                        p,
                                        context.language().partyBroadcast(),
                                        Placeholder.parsed("player", owner.getName()));
                            }
                        },
                        0L,
                        intervalTicks);
    }
}
