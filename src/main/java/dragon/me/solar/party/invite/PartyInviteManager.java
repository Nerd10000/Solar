package dragon.me.solar.party.invite;

import dragon.me.solar.Solar;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

public class PartyInviteManager {

    private List<PartyInviteRecord> inviteList = new ArrayList<>();

    public void expireTimer() {

        Bukkit.getScheduler()
                .runTaskTimer(
                        Solar.instance,
                        () -> {

                            // TODO: Implement

                        },
                        0,
                        20);
    }
}
