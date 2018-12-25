package de.a0zero.geofence4fhem.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;


/**
 * assign a profile {@link Profile} to execute ({@link GeofenceAction}) on enter/leave
 * of the given {@link GeofenceDto}
 */
@Entity(
        primaryKeys = { "profileId", "geofenceId" },
        foreignKeys = {
                @ForeignKey(
                        entity = GeofenceDto.class,
                        parentColumns = "id",
                        childColumns = "geofenceId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(
                        entity = Profile.class,
                        parentColumns = "ID",
                        childColumns = "profileId",
                        onDelete = ForeignKey.CASCADE)})
public class GeofenceProfiles {

    @NonNull
    private int profileId;

    @NonNull
    private String geofenceId;

    public GeofenceProfiles(int profileId, @NonNull String geofenceId) {
        this.profileId = profileId;
        this.geofenceId = geofenceId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(String geofenceId) {
        this.geofenceId = geofenceId;
    }
}
