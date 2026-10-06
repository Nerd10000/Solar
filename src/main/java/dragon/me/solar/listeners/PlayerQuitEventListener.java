package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.database.models.PlayerStore;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.MatchManager;
import dragon.me.solar.match.MatchService;
import dragon.me.solar.match.player.PlayerSnapshot;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.match.utils.MatchStageEnum;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.party.InMemoryParty;
import dragon.me.solar.party.PartyManager;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

    private final MatchManager matchManager;
    private final MatchService matchService;
    private final PartyManager partyManager;
    private final MessageService messageService;

    public PlayerQuitEventListener(
            MatchManager matchManager,
            MatchService matchService,
            PartyManager partyManager,
            MessageService messageService) {
        this.matchManager = matchManager;
        this.matchService = matchService;
        this.partyManager = partyManager;
        this.messageService = messageService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();

        /*
         *
         * Handling players that leave while in a party
         *
         */
        if (partyManager.isMemberOfAParty(uuid)) {

            InMemoryParty party = partyManager.getPartyByMember(uuid);

            if (party != null) {

                if (party.getOwner().equals(uuid)) {

                    partyManager.executeForEachMember(party, p -> {
                        messageService.send(
                                p,
                                messageService.language().partyOwnerLeft(),
                                Placeholder.parsed(
                                        "player", Bukkit.getOfflinePlayer(uuid).getName()));
                    });

                    partyManager.removeParty(party);

                } else {

                    partyManager.executeForEachMember(party, p -> {
                        messageService.send(
                                p,
                                messageService.language().playerLeftParty(),
                                Placeholder.parsed(
                                        "player", Bukkit.getOfflinePlayer(uuid).getName()));
                    });
                    party.getMemberList().remove(uuid);
                }
            }
        }

        /*
         *
         * Handling players that are in a match and quit
         *
         */
        if (!matchManager.isPlayerInAMatch(uuid)) {
            return;
        }

        InMemoryMatch match = matchManager.getMatchByMember(uuid);
        if (match == null || match.getStage() == MatchStageEnum.ENDED) {
            return;
        }

        Solar.cache.invalidatePlayerAll(uuid);

        for (InMemoryTeam team : match.getTeamList()) {
            team.getMembers().removeIf(member -> member.uuid().equals(uuid));
        }

        List<InMemoryTeam> aliveTeams = match.getTeamList().stream()
                .filter(team -> !team.getMembers().isEmpty())
                .toList();

        boolean matchOver = aliveTeams.size() <= 1;
        InMemoryTeam winner = matchOver && !aliveTeams.isEmpty() ? aliveTeams.getFirst() : null;

        if (matchOver) {
            matchService.finishMatch(match, winner, MatchEndReason.FORFEIT);
        }

        PlayerSnapshot snapshot = match.getSavedInventories().get(uuid);

        if (snapshot != null) {

            PlayerStore store = new PlayerStore();

            //            store.setArmor();
            //            store.setContent(snapshot.getContents());
            //            store.setUuid(uuid.toString());
            //            store.setOffhand(snapshot.getOffhand());
            //
            //            store.setPotionEffects(
            // snapshot.getEffects().toArray(PotionEffect[]::new));
            //
            //
            //            Solar.databaseManager.updateStore(store);

            player.getInventory().setContents(snapshot.getContents());
            player.getInventory().setArmorContents(snapshot.getArmor());
            player.getInventory().setItemInOffHand(snapshot.getOffhand());
            player.clearActivePotionEffects();

            player.updateInventory();
        }
    }
}
