package dragon.me.solar.match;

import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchEndReason;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.utils.SoundUtils;
import java.util.Iterator;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchAnnouncementService {
    private final ConfigManager configManager;
    private final MessageService messages;

    public MatchAnnouncementService(ConfigManager configManager, MessageService messages) {
        this.configManager = configManager;
        this.messages = messages;
    }

    public void announceResults(InMemoryMatch match, InMemoryTeam winner, String winnerName, MatchEndReason reason) {
        for (TeamPlayer teamPlayer : match.getMembers()) {
            Player player = Bukkit.getPlayer(teamPlayer.uuid());
            if (player == null) {
                continue;
            }

            boolean isWinner =
                    winner != null && match.getTeamByMember(teamPlayer.uuid()).equals(winner);
            if (reason == MatchEndReason.FORFEIT) {
                messages.send(
                        player,
                        isWinner
                                ? messages.language().matchForfeitWin()
                                : messages.language().matchForfeitLoss(),
                        Placeholder.parsed("winner", winnerName));
                continue;
            }

            SoundUtils.playConfiguredSound(
                    player,
                    isWinner
                            ? configManager.settingsRecord().soundRecords().wonSound()
                            : configManager.settingsRecord().soundRecords().lostSound(),
                    isWinner ? Sound.UI_TOAST_CHALLENGE_COMPLETE : Sound.BLOCK_BEACON_DEACTIVATE);
            messages.send(
                    player,
                    isWinner
                            ? messages.language().matchWon()
                            : messages.language().matchLost(),
                    Placeholder.parsed("winner", winnerName));
        }
    }

    public String resolveWinnerName(InMemoryTeam winner) {
        if (winner == null) {
            return "Unknown";
        }
        Iterator<TeamPlayer> iterator = winner.getMembers().iterator();
        if (!iterator.hasNext()) {
            return "Unknown";
        }
        Player player = Bukkit.getPlayer(iterator.next().uuid());
        return player != null ? player.getName() : "Unknown";
    }
}
