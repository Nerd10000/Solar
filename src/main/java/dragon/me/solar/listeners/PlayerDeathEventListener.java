package dragon.me.solar.listeners;

import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.match.utils.MatchStageEnum;
import dragon.me.solar.utils.SoundUtils;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();

        if (!Solar.matchManager.isPlayerInAMatch(player.getUniqueId())) {
            return;
        }

        InMemoryMatch match = Solar.matchManager.getMatchByMember(player.getUniqueId());
        if (match == null || match.getStage() == MatchStageEnum.ENDED) {
            return;
        }

        Optional<InMemoryTeam> teamOptional =
                match.getTeamList().stream()
                        .filter(
                                team ->
                                        team.getMembers().stream()
                                                .anyMatch(
                                                        teamPlayer ->
                                                                teamPlayer
                                                                        .uuid()
                                                                        .equals(
                                                                                player
                                                                                        .getUniqueId())))
                        .findFirst();

        if (teamOptional.isEmpty()) {
            return;
        }

        InMemoryTeam team = teamOptional.get();

        team.setMemberStateTo(false, player.getUniqueId());

        event.deathMessage(Component.text(""));

        if (player.getKiller() == null) {

            match.broadcast(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().playerDiedToUnknownCauses(),
                            Placeholder.parsed("victim", player.getName())));

        } else {

            match.broadcast(
                    Solar.miniMessage.deserialize(
                            Solar.configManager.languageRecord().playerDiedToPlayer(),
                            Placeholder.parsed("victim", player.getName()),
                            Placeholder.parsed("killer", player.getKiller().getName())));
        }

        team.updateTeamStatus();
        Location deathLocation = player.getLocation().clone().add(0, 1, 0);
        Bukkit.getScheduler()
                .runTask(
                        Solar.instance,
                        () -> {
                            if (player.isDead()) {
                                player.spigot().respawn();

                                player.teleport(deathLocation);
                                player.setAllowFlight(true);
                                player.setInvulnerable(true);
                            }
                        });

        if (!match.shouldEndMatch()) {
            SoundUtils.playConfiguredSound(
                    player,
                    Solar.configManager.settingsRecord().soundRecords().deadSound(),
                    Sound.ENTITY_BLAZE_DEATH);
            return;
        }

        InMemoryTeam winner =
                match.getTeamList().stream()
                        .filter(InMemoryTeam::isTeamAlive)
                        .findFirst()
                        .orElse(null);

        Solar.matchManager.endMatch(match, winner, MatchEndReason.DEATH);
    }
}
