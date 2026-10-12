package dragon.me.solar.hooks.intave;

import de.jpx3.intave.access.check.event.IntaveCommandExecutionEvent;
import dragon.me.solar.Solar;
import dragon.me.solar.match.InMemoryMatch;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IntavePunishmentListener implements Listener {

    @EventHandler
    public void onCommandExecution(IntaveCommandExecutionEvent e) {

        if (Solar.matchManager.isPlayerInAMatch(e.player().getUniqueId())) {

            InMemoryMatch match = Solar.matchManager.getMatchByMember(e.player().getUniqueId());

            for (TeamPlayer tp : match.getMembers()) {

                Player player = Bukkit.getPlayer(tp.uuid());

                if (player != null) {
                    SoundUtils.playConfiguredSound(
                            player,
                            Solar.configManager.settingsRecord().soundRecords().terminatedMatch(),
                            Sound.ENTITY_WITHER_DEATH);
                    Solar.messageService.sendTitle(
                            player,
                            Solar.messageService.language().matchTerminatedTitle(),
                            Solar.messageService.language().matchTerminatedSubtitle());
                }
            }

            Bukkit.getScheduler().runTask(Solar.instance, () -> {
                Solar.matchService.terminate(match);
            });
        }
    }
}
