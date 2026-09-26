package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class PartyInfoArg {

    private final PartyCommandContext context;

    public PartyInfoArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party info")
    public void info(CommandSourceStack stack) {

        if (!(stack.getSender() instanceof Player player)) {
            context.sendConsoleError(stack);
            return;
        }

        InMemoryParty party = context.partyManager().getPartyByMember(player.getUniqueId());

        if (party == null) {
            context.send(player, context.language().notInParty());
            return;
        }

        StringBuilder members = new StringBuilder();

        for (UUID member : party.getMemberList()) {
            members.append(Bukkit.getPlayer(member).getName() + " ");
        }

        List<String> raw = context.language().partyInfo();

        for (String s : raw) {
            context.send(
                    player,
                    s,
                    Placeholder.parsed(
                            "owner", Bukkit.getPlayer(party.getOwner()).getName()),
                    Placeholder.parsed("members", members.toString()));
        }
    }
}
