package de.a0zero.geofence4fhem.actions;

import com.google.android.gms.maps.model.LatLng;

import de.a0zero.geofence4fhem.data.GeofenceDto;
import io.reactivex.Observable;

public interface GeofenceAction<T> {

    Observable<ActionResponse> enter(GeofenceDto geofence, T profile, LatLng currentPosition);

    Observable<ActionResponse> leave(GeofenceDto geofence, T profile, LatLng currentPosition);

    interface ActionResponse {
        String message();
    }
}
