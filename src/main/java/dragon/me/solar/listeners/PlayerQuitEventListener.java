package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.database.models.PlayerStore;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.player.PlayerSnapshot;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.match.utils.MatchStageEnum;
import java.util.List;
import java.util.UUID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!Solar.matchManager.isPlayerInAMatch(uuid)) {
            return;
        }

        InMemoryMatch match = Solar.matchManager.getMatchByMember(uuid);
        if (match == null || match.getStage() == MatchStageEnum.ENDED) {
            return;
        }

        Solar.cache.invalidateAll(uuid);

        // 1. Remove the player from their team and decide the match outcome FIRST.
        //    Nothing below this point is allowed to be able to prevent this from
        //    having already happened — that was the original bug: a failing
        //    setHealth(0) call was aborting this logic before it could run.
        for (InMemoryTeam team : match.getTeamList()) {
            team.getMembers().removeIf(member -> member.uuid().equals(uuid));
        }

        List<InMemoryTeam> aliveTeams =
                match.getTeamList().stream().filter(team -> !team.getMembers().isEmpty()).toList();

        boolean matchOver = aliveTeams.size() <= 1;
        InMemoryTeam winner = matchOver && !aliveTeams.isEmpty() ? aliveTeams.getFirst() : null;

        if (matchOver) {
            Solar.matchManager.endMatch(match, winner, MatchEndReason.FORFEIT);
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

        }
    }
}
