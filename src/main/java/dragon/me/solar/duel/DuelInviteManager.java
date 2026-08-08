package dragon.me.solar.duel;

import dragon.me.solar.Solar;
import dragon.me.solar.duel.record.DuelInviteRecord;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DuelInviteManager {

    public List<DuelInviteRecord> duelInviteRecordList = new ArrayList<>();

    public DuelInviteManager(){



    }

    public boolean add(DuelInviteRecord record){

        if (hasInvite(record.sender())){
            return false;
        }

        duelInviteRecordList.add(record);

        return true;

    }

    public boolean hasInvite(UUID uuid){

        for (DuelInviteRecord d : duelInviteRecordList){

            if (d.sender() == uuid){
                return true;
            }

        }
        return false;
    }

    public void expireTimer(){

        Bukkit.getScheduler().runTaskTimer(Solar.instance, () -> {

            for (DuelInviteRecord d : duelInviteRecordList){

                if ((System.currentTimeMillis() - d.timestamp()) > Solar.configManager.settingsRecord().duelRequestExpireTime()){


                    Bukkit.getPlayer(d.sender()).sendMessage(
                            Solar.miniMessage.deserialize(
                                    Solar.configManager.languageRecord().duelRequestExpired(),
                                    Placeholder.parsed("player", Bukkit.getPlayer(d.receiver()).getName()),
                                    Placeholder.parsed("prefix", Solar.configManager.languageRecord().prefix())
                            )
                    );

                    duelInviteRecordList.remove(d);

                }

            }


        },0,20L);

    }
}
