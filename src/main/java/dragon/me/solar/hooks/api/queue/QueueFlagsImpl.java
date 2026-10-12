package dragon.me.solar.hooks.api.queue;

import dragon.me.solar.api.queue.QueueFlags;
import dragon.me.solar.configs.records.QueueFlagsRecord;

public class QueueFlagsImpl implements QueueFlags {

    private final QueueFlagsRecord record;

    public QueueFlagsImpl(QueueFlagsRecord record) {
        this.record = record;
    }

    @Override
    public String getWeight() {
        return record.weight();
    }

    @Override
    public int getTeamSize() {
        return record.teamSize();
    }

    @Override
    public boolean arePartiesAllowed() {
        return record.allowParties();
    }

    @Override
    public int getMinRating() {
        return record.minRating();
    }

    @Override
    public String getPermission() {
        return record.permission();
    }

    @Override
    public int getMinPartySize() {
        return record.minPartySize();
    }

    @Override
    public int getMaxPartySize() {
        return record.maxPartySize();
    }
}
