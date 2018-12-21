package de.a0zero.geofence4fhem.data;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;


/**
 * assign a profile {@link FhemProfile} to execute ({@link de.a0zero.geofence4fhem.actions.GeofenceAction}) on enter/leave
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
                        entity = FhemProfile.class,
                        parentColumns = "ID",
                        childColumns = "profileId",
                        onDelete = ForeignKey.CASCADE)},
        indices = @Index("geofenceId")
)
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
