package dragon.me.solar.commands.args.duel;

import dragon.me.solar.Solar;
import dragon.me.solar.duel.DuelInviteManager;
import dragon.me.solar.duel.record.DuelInviteRecord;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.messages.MessageService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;

public class DuelRequestArg {
    private final KitManager kitManager;
    private final DuelInviteManager duelInviteManager;
    private final MessageService messages;

    public DuelRequestArg(
            KitManager kitManager, DuelInviteManager duelInviteManager, MessageService messages) {
        this.kitManager = kitManager;
        this.duelInviteManager = duelInviteManager;
        this.messages = messages;
    }

    @Command("duel <player> <kit>")
    public void request(
            CommandSourceStack stack,
            @Argument("player") Player player,
            @Argument(value = "kit", suggestions = "kits") String kit,
            @Flag("rounds") @Default("1") Integer rounds,
            @Flag(value = "map", suggestions = "arenas") @Default("random") String map) {
        int requestedRounds = rounds == null ? 1 : rounds;
        String requestedMap = map == null ? "random" : map;

        if (!(stack.getSender() instanceof Player sender)) {
            messages.sendConsoleError(stack);
            return;
        }
        if (Solar.MAINTENANCE_MODE) {
            messages.send(sender, messages.language().maintenancePrevention());
            return;
        }

        InMemoryKit inMemoryKit = kitManager.getKit(kit);
        if (inMemoryKit == null) {
            messages.send(
                    sender, messages.language().kitNotFound(), Placeholder.parsed("kit", kit));
            return;
        }

        DuelInviteRecord record =
                new DuelInviteRecord(
                        sender.getUniqueId(),
                        player.getUniqueId(),
                        kit,
                        requestedMap,
                        requestedRounds,
                        System.currentTimeMillis());
        if (!duelInviteManager.add(record)) {
            messages.send(sender, messages.language().tooManyDuelInvites());
            return;
        }

        messages.send(
                sender,
                messages.language().duelRequestSent(),
                Placeholder.parsed("player", player.getName()),
                Placeholder.parsed("rounds", String.valueOf(requestedRounds)),
                Placeholder.parsed("map", requestedMap),
                Placeholder.parsed("kit", kit));
        messages.send(
                player,
                messages.language().duelRequestReceived(),
                Placeholder.parsed("player", sender.getName()),
                Placeholder.parsed("rounds", String.valueOf(requestedRounds)),
                Placeholder.parsed("kit", kit),
                Placeholder.parsed("map", requestedMap));
    }
}
