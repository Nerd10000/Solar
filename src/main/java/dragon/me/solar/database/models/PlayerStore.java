package dragon.me.solar.database.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_store")
public class PlayerStore {

    private String uuid;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String armor;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String content;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String offhand;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String potionEffects;

    public PlayerStore() {}

    public String getArmor() {
        return armor;
    }

    public void setArmor(String armor) {
        this.armor = armor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOffhand() {
        return offhand;
    }

    public void setOffhand(String offhand) {
        this.offhand = offhand;
    }

    public String getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(String potionEffects) {
        this.potionEffects = potionEffects;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
