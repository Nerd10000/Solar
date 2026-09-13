package dragon.me.solar.commands;

import com.sk89q.worldedit.math.BlockVector3;
import dragon.me.solar.Solar;
import dragon.me.solar.arena.InMemoryArena;
import dragon.me.solar.hooks.FaweHook;
import dragon.me.solar.kit.InMemoryKit;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.invite.PartyInviteRecord;
import dragon.me.solar.utils.SoundUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;

public class PartyCommand {

    @Command("party create")
    public void create(CommandSourceStack stack) {
        if (!(stack.getSender() instanceof Player p)) {
            sendConsoleError(stack);
            return;
        }

        if (Solar.partyManager.isMemberOfAParty(p.getUniqueId())) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantCreatePartyAsMemberOfAParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryParty party = new InMemoryParty(UUID.randomUUID(), p.getUniqueId());
        Solar.partyManager.addParty(party);

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().partyCreated(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));
    }

    @Command("party leave")
    public void leave(CommandSourceStack stack) {
        if (!(stack.getSender() instanceof Player p)) {
            sendConsoleError(stack);
            return;
        }

        if (!Solar.partyManager.isMemberOfAParty(p.getUniqueId())) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notInParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryParty party = Solar.partyManager.getPartyByMember(p.getUniqueId());
        if (party == null) return;

        // If party owner leaves, disband the party instead
        if (party.getOwner().equals(p.getUniqueId())) {
            disband(stack);
            return;
        }

        party.getMemberList().remove(p.getUniqueId());

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().playerLeftParty(),
                        Placeholder.parsed(
                                "prefix", Solar.configManager.languageRecord().prefix())));

        // Notify other members
        Solar.partyManager.executeForEachMember(
                party,
                member -> {
                    member.sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().playerLeftParty(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("player", p.getName())));
                });
    }

    @Command("party disband")
    public void disband(CommandSourceStack stack) {
        if (!(stack.getSender() instanceof Player p)) {
            sendConsoleError(stack);
            return;
        }

        InMemoryParty party = Solar.partyManager.getPartyByMember(p.getUniqueId());

        if (party == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notInParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        if (!party.getOwner().equals(p.getUniqueId())) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantDoPartyAsMemberOnly(),
                            Placeholder.parsed("action", "disband"),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        Solar.partyManager.removeParty(party);

        Solar.partyManager.executeForEachMember(
                party,
                player -> {
                    player.sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().partyDisbanded(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("owner", p.getName())));

                    SoundUtils.playConfiguredSound(
                            player,
                            Solar.configManager.settingsRecord().soundRecords().partyDisbanded(),
                            Sound.ENTITY_GOAT_HORN_BREAK);
                });
    }

    @Command("party invite <player>")
    public void invite(CommandSourceStack stack, @Argument("player") Player target) {
        if (!(stack.getSender() instanceof Player p)) {
            sendConsoleError(stack);
            return;
        }

        if (p.getUniqueId().equals(target.getUniqueId())) {
            /*p.sendMessage(Solar.miniMessage.deserialize(
                   Solar.configManager.languageRecord().cantInviteSelf(),
                   Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix())));

            */
            return;
        }

        InMemoryParty party = Solar.partyManager.getPartyByMember(p.getUniqueId());

        // Auto-create party if player isn't in one when inviting someone
        if (party == null) {
            party = new InMemoryParty(UUID.randomUUID(), p.getUniqueId());
            Solar.partyManager.addParty(party);
        } else if (!party.getOwner().equals(p.getUniqueId())) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantDoPartyAsMemberOnly(),
                            Placeholder.parsed("action", "invite"),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        if (Solar.partyManager.isMemberOfAParty(target.getUniqueId())) {
            /*p.sendMessage(Solar.miniMessage.deserialize(
                   Solar.configManager.languageRecord().part,
                   Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                   Placeholder.parsed("player", target.getName())));
            */
            return;
        }

        PartyInviteRecord invite =
                new PartyInviteRecord(
                        p.getUniqueId(),
                        target.getUniqueId(),
                        party.getUuid(),
                        System.currentTimeMillis());

        Solar.partyInviteManager.addInvite(invite);

        p.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().partyInviteSent(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("player", target.getName())));

        target.sendMessage(
                Solar.miniMessage.deserialize(
                        Solar.configManager.languageRecord().partyInviteReceived(),
                        Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix()),
                        Placeholder.parsed("sender", p.getName())));
    }

    @Command("party accept <player>")
    public void accept(CommandSourceStack stack, @Argument("player") Player inviter) {
        if (!(stack.getSender() instanceof Player p)) {
            sendConsoleError(stack);
            return;
        }

        if (Solar.partyManager.isMemberOfAParty(p.getUniqueId())) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().cantCreatePartyAsMemberOfAParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        PartyInviteRecord invite =
                Solar.partyInviteManager.getInviteBySender(inviter.getUniqueId());
        if (invite == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notInviteFromParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryParty party = Solar.partyManager.getPartyByMember(inviter.getUniqueId());
        if (party == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().noSuchParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            Solar.partyInviteManager.removeInvite(invite);
            return;
        }

        party.getMemberList().add(p.getUniqueId());
        Solar.partyInviteManager.removeInvite(invite);

        Solar.partyManager.executeForEachMember(
                party,
                member -> {
                    member.sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().playerJoinedParty(),
                                    Placeholder.parsed(
                                            "prefix",
                                            Solar.configManager.languageRecord().prefix()),
                                    Placeholder.parsed("player", p.getName())));
                });
    }

    @Command("party start <kit> <eventType>")
    public void start(
            CommandSourceStack stack,
            @Argument("kit") String kit,
            @Argument("eventType") String eventType) {
        if (!(stack.getSender() instanceof Player p)) {
            sendConsoleError(stack);
            return;
        }

        InMemoryParty party = Solar.partyManager.getPartyByMember(p.getUniqueId());

        if (party == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notInParty(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        if (!party.getOwner().equals(p.getUniqueId())) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notPartyLeader(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix())));
            return;
        }

        InMemoryKit inMemoryKit = Solar.kitManager.getKit(kit);
        if (inMemoryKit == null) {
            p.sendMessage(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().kitNotFound(),
                            Placeholder.parsed(
                                    "prefix", Solar.configManager.languageRecord().prefix()),
                            Placeholder.parsed("kit", kit)));
            return;
        }

        switch (eventType.toLowerCase()) {
            case "splitfight":
            case "split":
                if (party.getMemberList().size() < 2) {
                    /*p.sendMessage(Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().notEnoughPartyMembers(),
                            Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix())
                    ));
                    TODO
                     */
                    return;
                }

                List<InMemoryTeam> teams = Solar.partyManager.generateTeamsForSplit(party);
                int slot = Solar.gridManager.allocate();
                BlockVector3 center = Solar.gridManager.getCenter(slot);
                String arenaName = resolveArenaName("random");

                if (arenaName == null) {
                    Solar.gridManager.free(slot);
                    /*p.sendMessage(Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().
                            Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix())
                    ));
                     */
                    return;
                }

                FaweHook.pasteArena(arenaName, center)
                        .thenAccept(
                                success -> {
                                    if (!success) {
                                        Solar.gridManager.free(slot);
                                        Solar.instance
                                                .getLogger()
                                                .warning("Failed to paste arena " + arenaName);
                                        return;
                                    }

                                    Bukkit.getScheduler()
                                            .runTask(
                                                    Solar.instance,
                                                    () ->
                                                            startSplitMatch(
                                                                    teams,
                                                                    inMemoryKit,
                                                                    arenaName,
                                                                    slot));
                                })
                        .exceptionally(
                                throwable -> {
                                    Solar.gridManager.free(slot);
                                    Solar.instance
                                            .getLogger()
                                            .log(
                                                    Level.SEVERE,
                                                    "Error while pasting arena " + arenaName,
                                                    throwable);
                                    return null;
                                });
                break;

            default:
                p.sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().invalidEventType(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix())));
                break;
        }
    }

    private void startSplitMatch(
            List<InMemoryTeam> teamList, InMemoryKit kit, String arenaName, int slot) {
        World world = Bukkit.getWorld("arenas");
        if (world == null) {
            Solar.gridManager.free(slot);
            Solar.instance.getLogger().severe("Arena world does not exist!");
            return;
        }

        InMemoryArena arena = Solar.arenaManager.getArena(arenaName);
        if (arena == null || arena.spawn1 == null || arena.spawn2 == null) {
            Solar.gridManager.free(slot);
            Solar.instance.getLogger().warning("Arena spawn points missing for " + arenaName);
            return;
        }

        BlockVector3 center = Solar.gridManager.getCenter(slot);
        Location team1Spawn = arena.spawn1.toLocation(world, center);
        Location team2Spawn = arena.spawn2.toLocation(world, center);

        for (TeamPlayer teamPlayer1 : teamList.get(0).getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer1.uuid());
            if (player != null) player.teleport(team1Spawn);
        }

        for (TeamPlayer teamPlayer2 : teamList.get(1).getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer2.uuid());
            if (player != null) player.teleport(team2Spawn);
        }

        Solar.matchManager.startMatch(teamList, kit.kitId, arenaName, arenaName, slot);
        Solar.instance
                .getLogger()
                .info("Starting the match between " + teamList.size() + " teams. (Party)");
    }

    private String resolveArenaName(String map) {
        if (map.equalsIgnoreCase("random")) {
            if (Solar.arenaManager.ARENAS.isEmpty()) {
                return null;
            }

            return Solar.arenaManager.ARENAS.values().stream()
                    .skip(ThreadLocalRandom.current().nextInt(Solar.arenaManager.ARENAS.size()))
                    .findFirst()
                    .orElseThrow()
                    .name;
        }

        if (!Solar.arenaManager.ARENAS.containsKey(map)) {
            return null;
        }

        return map;
    }

    private void sendConsoleError(CommandSourceStack stack) {
        stack.getSender()
                .sendMessage(
                        Solar.miniMessage.deserialize(
                                Solar.configManager.languageRecord().consoleCantRun(),
                                Placeholder.parsed(
                                        "prefix", Solar.configManager.languageRecord().prefix())));
    }
}
