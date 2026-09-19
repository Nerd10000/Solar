package dragon.me.solar.commands.args.duel;

import dragon.me.solar.Solar;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class DuelDeclineArg {
    private final DuelInviteManager duelInviteManager;
    private final MessageService messages;

    public DuelDeclineArg(DuelInviteManager duelInviteManager, MessageService messages) {
        this.duelInviteManager = duelInviteManager;
        this.messages = messages;
    }

    @Command("duel decline <player>")
    public void decline(CommandSourceStack stack, @Argument("player") Player target) {
        if (!(stack.getSender() instanceof Player player)) {
            messages.sendConsoleError(stack);
            return;
        }
        if (Solar.MAINTENANCE_MODE) {
            messages.send(player, messages.language().maintenancePrevention());
        }

        messages.send(
                player,
                messages.language().duelDeclined(),
                Placeholder.parsed("sender", target.getName()));
        duelInviteManager.removeBySender(target.getUniqueId());
        messages.send(
                target,
                messages.language().duelDeclinedRequester(),
                Placeholder.parsed("target", player.getName()));
    }
}
