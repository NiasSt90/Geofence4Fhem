package de.a0zero.geofence4fhem.data;

import androidx.room.Entity;


/**
 * a profile with some settings, needed for executing on enter/leave of an {@link de.a0zero.geofence4fhem.actions.GeofenceAction} implementation.
 *
 * TODO: because no JOIN-TABLE inheritance not possible in sqlite we need to merge together FhemProfile and Profile into a single class.
 * Profile then can be used for other stuff (change RingTone-Settings, toggle-WLAN/..) and therefore the attributes here must
 * be stored into a json structure blob...
 */
@Entity
public class FhemProfile extends Profile {

    private String fhemUrl;

    private String username;

    private String password;

    private String deviceUUID;

    public FhemProfile() {
        super(ProfileType.FHEM_NOTIFY);
    }

    public String getFhemUrl() {
        return fhemUrl;
    }

    public void setFhemUrl(String fhemUrl) {
        this.fhemUrl = fhemUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }
}
