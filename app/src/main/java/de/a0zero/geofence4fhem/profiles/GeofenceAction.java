package de.a0zero.geofence4fhem.profiles;

import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import de.a0zero.geofence4fhem.data.entities.Profile;
import io.reactivex.Flowable;


public interface GeofenceAction {

	Flowable<ActionResponse> enter(GeofenceDto geofence, Profile profile, LatLng currentPosition);

	Flowable<ActionResponse> leave(GeofenceDto geofence, Profile profile, LatLng currentPosition);

	interface ActionResponse {

		String message();
	}


	interface ErrorActionResponse extends ActionResponse {

	}

	static ErrorActionResponse fromThrowable(Throwable t) {
		return t::getMessage;
	}

	;
}
