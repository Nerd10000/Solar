package dragon.me.solar.commands.args.party;

import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.utils.SoundUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;

public class PartyDisbandArg {
    private final PartyCommandContext context;

    public PartyDisbandArg(PartyCommandContext context) {
        this.context = context;
    }

    @Command("party disband")
    public void disband(CommandSourceStack stack) {
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
            context.send(player, context.language().cantDoPartyAsMemberOnly(), Placeholder.parsed("action", "disband"));
            return;
        }

        context.partyManager().removeParty(party);
        context.partyManager().executeForEachMember(party, member -> {
            context.send(member, context.language().partyDisbanded(), Placeholder.parsed("owner", player.getName()));
            SoundUtils.playConfiguredSound(
                    member,
                    context.configManager().settingsRecord().soundRecords().partyDisbanded(),
                    Sound.ENTITY_GOAT_HORN_BREAK);
        });
    }
}
