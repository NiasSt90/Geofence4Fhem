package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.Entity;

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
