package dragon.me.solar.match;

import dragon.me.solar.match.player.PlayerSnapshot;
import dragon.me.solar.match.teams.InMemoryTeam;
import dragon.me.solar.match.utils.MatchStageEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InMemoryMatch {
    private final UUID uuid;
    private List<InMemoryTeam> teamList = new ArrayList<>();

    private String kit;
    private String map;
    private String arenaName;
    private int gridSlot = -1;

    private InMemoryTeam winner;
    private List<UUID> spectatorList = new ArrayList<>();
    private MatchStageEnum stage = MatchStageEnum.STARTING;
    private final Map<UUID, PlayerSnapshot> savedInventories = new HashMap<>();

    private boolean preventAllActions = false;

    public InMemoryMatch(List<InMemoryTeam> teamList, String kit, String map) {
        uuid = UUID.randomUUID();
        this.teamList = teamList;
        this.kit = kit;
        this.map = map;
    }

    public List<InMemoryTeam> getTeamList() {
        return teamList;
    }

    public void broadcast(Component component) {

        for (InMemoryTeam team : teamList) {

            team.broadcastMessage(component);
        }

        for (UUID spectator : spectatorList) {

            Player player = Bukkit.getPlayer(spectator);
            if (player != null) {
                player.sendMessage(component);
            }
        }
    }

    public boolean shouldEndMatch() {

        long count = teamList.stream().filter(InMemoryTeam::isTeamAlive).count();

        return count <= 1;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getKit() {
        return kit;
    }

    public String getMap() {
        return map;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public int getGridSlot() {
        return gridSlot;
    }

    public void setGridSlot(int gridSlot) {
        this.gridSlot = gridSlot;
    }

    public InMemoryTeam getWinner() {
        return winner;
    }

    public void setWinner(InMemoryTeam winner) {
        this.winner = winner;
    }

    public List<UUID> getSpectatorList() {
        return spectatorList;
    }

    public void setSpectatorList(List<UUID> spectatorList) {
        this.spectatorList = spectatorList;
    }

    public MatchStageEnum getStage() {
        return stage;
    }

    public void setStage(MatchStageEnum stage) {
        this.stage = stage;
    }

    public Map<UUID, PlayerSnapshot> getSavedInventories() {
        return savedInventories;
    }

    public void saveInventory(Player player) {
        savedInventories.put(player.getUniqueId(), PlayerSnapshot.capture(player));
    }

    public boolean isPreventAllActions() {
        return preventAllActions;
    }

    public void setPreventAllActions(boolean preventAllActions) {
        this.preventAllActions = preventAllActions;
    }
}
