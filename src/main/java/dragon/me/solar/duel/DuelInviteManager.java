package dragon.me.solar.duel;

import dragon.me.solar.Solar;
import dragon.me.solar.duel.record.DuelInviteRecord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DuelInviteManager {

    public List<DuelInviteRecord> duelInviteRecordList = new ArrayList<>();

    public DuelInviteManager() {}

    public boolean add(DuelInviteRecord record) {

        if (hasInvite(record.sender())) {
            return false;
        }

        duelInviteRecordList.add(record);

        return true;
    }

    public boolean hasInvite(UUID uuid) {

        for (DuelInviteRecord d : duelInviteRecordList) {

            if (d.sender() == uuid) {
                return true;
            }
        }
        return false;
    }

    public void expireTimer() {
        Bukkit.getScheduler()
                .runTaskTimer(
                        Solar.instance,
                        () -> {
                            long now = System.currentTimeMillis();
                            long expireTime =
                                    Solar.configManager.settingsRecord().duelRequestExpireTime();

                            Iterator<DuelInviteRecord> iterator = duelInviteRecordList.iterator();

                            while (iterator.hasNext()) {
                                DuelInviteRecord invite = iterator.next();

                                if (now - invite.timestamp() > expireTime) {

                                    Player sender = Bukkit.getPlayer(invite.sender());
                                    Player receiver = Bukkit.getPlayer(invite.receiver());

                                    if (sender != null && receiver != null) {
                                        sender.sendMessage(
                                                Solar.miniMessage.deserialize(
                                                        Solar.configManager
                                                                .languageRecord()
                                                                .duelRequestExpired(),
                                                        Placeholder.parsed(
                                                                "player", receiver.getName()),
                                                        Placeholder.parsed(
                                                                "prefix",
                                                                Solar.configManager
                                                                        .languageRecord()
                                                                        .prefix())));
                                    }

                                    iterator.remove();
                                }
                            }
                        },
                        0L,
                        20L);
    }
}
