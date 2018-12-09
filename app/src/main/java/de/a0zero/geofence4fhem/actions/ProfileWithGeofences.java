package de.a0zero.geofence4fhem.actions;

import de.a0zero.geofence4fhem.data.GeofenceDto;

public class ProfileWithGeofences {

    public GeofenceDto geofenceDto;

    public boolean active;

    public ProfileWithGeofences(GeofenceDto geofenceDto, boolean active) {
        this.geofenceDto = geofenceDto;
        this.active = active;
    }
}
