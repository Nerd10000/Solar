package dragon.me.solar.configs.records;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public record LobbyRecord(double x, double y, double z, float yaw, float pitch, String name) {}
