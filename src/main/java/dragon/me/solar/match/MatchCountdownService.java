package dragon.me.solar.match;

import dragon.me.solar.Solar;
import dragon.me.solar.configs.ConfigManager;
import dragon.me.solar.match.player.TeamPlayer;
import dragon.me.solar.match.utils.MatchStageEnum;
import dragon.me.solar.messages.MessageService;
import dragon.me.solar.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchCountdownService {
    private final Solar plugin;
    private final ConfigManager configManager;
    private final MessageService messages;

    public MatchCountdownService(
            Solar plugin, ConfigManager configManager, MessageService messages) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.messages = messages;
    }

    public void start(InMemoryMatch match) {
        final int seconds = 3;
        for (int i = 0; i < seconds; i++) {
            int secondsLeft = seconds - i;
            Bukkit.getScheduler()
                    .runTaskLater(
                            plugin,
                            () -> {
                                String sound =
                                        configManager
                                                .settingsRecord()
                                                .soundRecords()
                                                .countdownSound();
                                for (TeamPlayer teamPlayer : match.getMembers()) {
                                    Player player = Bukkit.getPlayer(teamPlayer.uuid());
                                    if (player == null) {
                                        continue;
                                    }
                                    SoundUtils.playConfiguredSound(
                                            player, sound, Sound.BLOCK_NOTE_BLOCK_PLING);
                                    messages.send(
                                            player,
                                            "<gray>Match starts in <color:#FCD05C>"
                                                    + secondsLeft
                                                    + "</color>...</gray>");
                                }
                            },
                            i * 20L);
        }

        Bukkit.getScheduler()
                .runTaskLater(
                        plugin,
                        () -> {
                            match.setStage(MatchStageEnum.ONGOING);
                            for (TeamPlayer teamPlayer : match.getMembers()) {
                                Player player = Bukkit.getPlayer(teamPlayer.uuid());
                                if (player == null) {
                                    continue;
                                }
                                SoundUtils.playConfiguredSound(
                                        player,
                                        configManager.settingsRecord().soundRecords().startSound(),
                                        Sound.BLOCK_BEACON_ACTIVATE);
                                messages.send(player, messages.language().matchBegin());
                            }
                        },
                        seconds * 20L);
    }
}
