package dragon.me.solar.commands.args.party;

import dragon.me.solar.Solar;
import dragon.me.solar.arena.ArenaManager;
import dragon.me.solar.arena.GridManager;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.kit.KitManager;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.PartyManager;
import dragon.me.solar.party.PartyService;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyStartArg {

    private final PartyManager partyManager;
    private final PartyService partyService;
    private final KitManager kitManager;
    private final ArenaManager arenaManager;
    private final GridManager gridManager;
    private final MatchService matchService;
    private final MessageService messages;

    public PartyStartArg(
            PartyManager partyManager,
            PartyService partyService,
            KitManager kitManager,
            ArenaManager arenaManager,
            GridManager gridManager,
            MatchService matchService,
            MessageService messages) {

        this.partyManager = partyManager;
        this.partyService = partyService;
        this.kitManager = kitManager;
        this.arenaManager = arenaManager;
        this.gridManager = gridManager;
        this.matchService = matchService;
        this.messages = messages;
    }

    @Command("party start <kit> <eventType>")
    public void start(CommandSourceStack stack, @Argument("kit") String kit, @Argument("eventType") String eventType) {

        if (!(stack.getSender() instanceof Player player)) {
            messages.sendConsoleError(stack);
            return;
        }

        if (Solar.MAINTENANCE_MODE) {
            messages.send(player, Solar.configManager.languageRecord().maintenancePrevention());
            return;
        }

        InMemoryParty party = partyManager.getPartyByMember(player.getUniqueId());

        if (party == null) {
            messages.send(player, messages.language().notInParty());
            return;
        }

        if (!party.getOwner().equals(player.getUniqueId())) {
            messages.send(player, messages.language().notPartyLeader());
            return;
        }

        InMemoryKit inMemoryKit = kitManager.getKit(kit);

        if (inMemoryKit == null) {
            messages.send(player, messages.language().kitNotFound(), Placeholder.parsed("kit", kit));
            return;
        }

        switch (eventType.toLowerCase()) {
            case "splitfight":
            case "split":
                startSplit(party, inMemoryKit);
                break;

            case "ffa":
                startFFA(party, inMemoryKit);
                break;

            default:
                messages.send(player, messages.language().invalidEventType());
                break;
        }
    }

    private void startFFA(InMemoryParty party, InMemoryKit kit) {
        if (party.getMemberList().size() < 2) {
            messages.send(
                    Bukkit.getPlayer(party.getOwner()), messages.language().notEnoughPlayers());
            return;
        }

        String arenaName = resolveArenaName();

        if (arenaName == null) {
            Solar.instance.getLogger().warning("Unable to start party ffa: no arenas are available.");
            return;
        }

        List<InMemoryTeam> teams = partyService.generateTeamsForFfa(party);

        int slot = gridManager.allocate();

        if (slot < 0) {
            Solar.instance.getLogger().warning("Unable to start party ffa: no grid slots are available.");
            return;
        }

        InMemoryMatch match = new InMemoryMatch(teams, kit.kitId);

        match.setArenaName(arenaName);

        match.setGridSlot(slot);

        Solar.instance
                .getLogger()
                .info("Starting party ffa: arena=" + arenaName + ", slot=" + slot + ", teams=" + teams.size());

        matchService.startMatch(match, true, null);
    }

    private void startSplit(InMemoryParty party, InMemoryKit kit) {
        if (party.getMemberList().size() < 2) {
            messages.send(
                    Bukkit.getPlayer(party.getOwner()), messages.language().notEnoughPlayers());
            return;
        }

        String arenaName = resolveArenaName();

        if (arenaName == null) {
            Solar.instance.getLogger().warning("Unable to start party split: no arenas are available.");
            return;
        }

        List<InMemoryTeam> teams = partyService.generateTeamsForSplit(party);

        int slot = gridManager.allocate();

        if (slot < 0) {
            Solar.instance.getLogger().warning("Unable to start party split: no grid slots are available.");
            return;
        }

        InMemoryMatch match = new InMemoryMatch(teams, kit.kitId);

        match.setArenaName(arenaName);

        match.setGridSlot(slot);

        Solar.instance
                .getLogger()
                .info("Starting party split: arena=" + arenaName + ", slot=" + slot + ", teams=" + teams.size());

        matchService.startMatch(match, false, null);
    }

    private String resolveArenaName() {
        if (arenaManager.getArenas().isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(arenaManager.getArenas().size());

        return arenaManager.getArenas().values().stream()
                .skip(index)
                .findFirst()
                .map(arena -> arena.name)
                .orElse(null);
    }
}
