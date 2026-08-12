package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public record SettingsRecord(
        @Setting("arena") GlobalArenaSettingsRecord arena,
        @Setting("duel-request-expire-time") long duelRequestExpireTime,
        @Setting("lobby-location") LobbyRecord lobbyRecord,
        @Setting("sounds") SoundRecords soundRecords,
        @Setting("teleport-to-lobby-on-join") boolean teleportToLobbyOnJoin,
        @Setting("party-invite-expire-time") long partyInviteExpireTime) {

    public static final SettingsRecord DEFAULTS =
            new SettingsRecord(
                    GlobalArenaSettingsRecord.DEFAULTS,
                    120000,
                    new LobbyRecord(0, 0, 0, 0f, 0f, "world"),
                    new SoundRecords(
                            "BLOCK_NOTE_BLOCK_PLING",
                            "BLOCK_BEACON_ACTIVATE",
                            "BLOCK_BEACON_DEACTIVATE",
                            "ENTITY_BLAZE_DEATH",
                            "UI_TOAST_CHALLENGE_COMPLETE"),
                    true,
                    120000);

    public SettingsRecord updateLobbyLocation(LobbyRecord record) {

        SettingsRecord prev =
                new SettingsRecord(
                        this.arena,
                        this.duelRequestExpireTime,
                        record,
                        this.soundRecords,
                        this.teleportToLobbyOnJoin,
                        this.partyInviteExpireTime);

        return prev;
    }
}
