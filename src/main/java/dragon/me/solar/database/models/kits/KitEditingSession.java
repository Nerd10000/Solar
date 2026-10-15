package dragon.me.solar.database.models.kits;

import dragon.me.solar.commands.args.kit.PlayerInventorySnapshot;
import java.util.UUID;

public record KitEditingSession(UUID uuid, String kitId, PlayerInventorySnapshot snapshot) {}
