package dragon.me.solar.database.models.kits;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "kit_layouts")
public class KitLayout {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String playerId;

    @DatabaseField(canBeNull = false, uniqueCombo = true)
    private String kitId;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] armor;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] contents;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    private byte[] offhand;

    public KitLayout() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getKitId() {
        return kitId;
    }

    public void setKitId(String kitId) {
        this.kitId = kitId;
    }

    public byte[] getArmor() {
        return armor;
    }

    public void setArmor(byte[] armor) {
        this.armor = armor;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public byte[] getOffhand() {
        return offhand;
    }

    public void setOffhand(byte[] offhand) {
        this.offhand = offhand;
    }
}
