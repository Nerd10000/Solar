package dragon.me.solar.party.invite;

import dragon.me.solar.Solar;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class PartyInviteManager {

    private List<PartyInviteRecord> inviteList = new ArrayList<>();

    public PartyInviteManager() {}

    public void addInvite(PartyInviteRecord invite) {
        inviteList.add(invite);
    }

    public boolean removeInvite(PartyInviteRecord invite) {

        if (inviteList.contains(invite)) {

            inviteList.remove(invite);

            return true;
        }

        return false;
    }

    public @Nullable PartyInviteRecord getInviteBySender(UUID sender) {

        return inviteList.stream()
                .filter(inv -> inv.sender().equals(sender))
                .findFirst()
                .orElse(null);
    }

    public void expireTimer() {

        Bukkit.getScheduler()
                .runTaskTimer(
                        Solar.instance,
                        () -> {
                            for (PartyInviteRecord invite : inviteList) {

                                if ((System.currentTimeMillis() - invite.timestamp())
                                        > Solar.configManager
                                                .settingsRecord()
                                                .partyInviteExpireTime()) {

                                    inviteList.remove(invite);
                                }
                            }
                        },
                        0,
                        20);
    }
}
