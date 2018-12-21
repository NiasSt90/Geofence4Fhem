package de.a0zero.geofence4fhem.data;

import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

public class Profile {

    @PrimaryKey(autoGenerate = true)
    int ID;

    String label;

    ProfileType type;

    public Profile() {
    }

    @Ignore
    public Profile(ProfileType type) {
        this.type = type;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @TypeConverters(TypeConvertes.class)
    public void setType(ProfileType type) {
        this.type = type;
    }

    @TypeConverters(TypeConvertes.class)
    public ProfileType getType() {
        return type;
    }
}
