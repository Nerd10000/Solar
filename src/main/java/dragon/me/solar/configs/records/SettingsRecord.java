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
        @Setting("party-invite-expire-time") long partyInviteExpireTime,
        @Setting("rating-settings") RatingSettingsRecord ratingSettings) {

    public static final SettingsRecord DEFAULTS = new SettingsRecord(
            GlobalArenaSettingsRecord.DEFAULTS,
            120000,
            new LobbyRecord(0, 0, 0, 0f, 0f, "world"),
            new SoundRecords(
                    "block.note.block.pling",
                    "block.beacon.activate",
                    "block.beacon.deactivate",
                    "entity.blaze.death",
                    "ui.toast.challange.complete",
                    "block.note_block.bass",
                    "entity.player.levelup"),
            true,
            120000,
            RatingSettingsRecord.DEFAULT);

    public SettingsRecord updateLobbyLocation(LobbyRecord record) {

        SettingsRecord prev = new SettingsRecord(
                this.arena,
                this.duelRequestExpireTime,
                record,
                this.soundRecords,
                this.teleportToLobbyOnJoin,
                this.partyInviteExpireTime,
                this.ratingSettings);

        return prev;
    }
}
