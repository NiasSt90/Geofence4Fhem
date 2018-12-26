package de.a0zero.geofence4fhem.transition;

import android.content.Context;
import android.content.Intent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.app.App;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfileState;
import de.a0zero.geofence4fhem.data.entities.Profile;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;
import io.reactivex.Flowable;


public class GeofenceProfileExecutor {

	private static final String TAG = GeofenceProfileExecutor.class.getSimpleName();

	private final Context context;


	public GeofenceProfileExecutor(Context context) {
		this.context = context;
	}


	public Flowable<GeofenceProfileState> execute(GeofenceDto fence, LatLng pos, int transition) {
		return App.geofenceActionRepo().listProfilesForGeofence(fence.getId())
				.flattenAsFlowable(t -> t)
				.flatMap(profile -> action(profile, fence, pos, transition))
				.doOnNext(state -> App.geofenceStateRepo().add(state))
				.doOnTerminate(this::startUpdateNotificationIntentService);
	}


	private Flowable<GeofenceProfileState> action(Profile profile, GeofenceDto fence, LatLng pos, int transition) {
		Flowable<GeofenceAction.ActionResponse> resultAction;
		GeofenceAction profileAction = profile.getType().action();
		switch (transition) {
			case Geofence.GEOFENCE_TRANSITION_ENTER:
				resultAction = profileAction.enter(fence, profile, pos);
				break;
			case Geofence.GEOFENCE_TRANSITION_DWELL:
				resultAction = profileAction.enter(fence, profile, pos);
				break;
			case Geofence.GEOFENCE_TRANSITION_EXIT:
				resultAction = profileAction.leave(fence, profile, pos);
				break;
			default:
				resultAction = Flowable.error(new IllegalStateException("Unknown transition type"));
				break;
		}
		return resultAction
				.retry(1)//TODO: move retry to action implementation
				.onErrorResumeNext(t -> {
					return Flowable.just(GeofenceAction.fromThrowable(t));
				})
				.map(resp -> new GeofenceProfileState(profile.getID(), fence.getId(), pos, transition)
						.assignResponse(resp));
	}


	private void startUpdateNotificationIntentService() {
		context.startService(new Intent(context, UpdateNotificationIntentService.class));
	}

}
