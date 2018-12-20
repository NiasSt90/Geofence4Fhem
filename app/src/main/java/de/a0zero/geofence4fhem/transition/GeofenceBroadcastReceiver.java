package de.a0zero.geofence4fhem.transition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.actions.GeofenceAction;
import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.FhemProfile;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.GeofenceProfileState;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;


/**
 * Receiver for geofence transition. execute all registered profile-actions for each geofence transition and
 * call {@link UpdateNotificationIntentService}
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION_GEOFENCE_UPDATE = "GeofenceUpdate";

	private static final String TAG = "GeofenceBroadcastRcv";


	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action != null) {
			switch (action) {
				case ACTION_GEOFENCE_UPDATE:
					onHandleGeofenceIntent(context, intent);
					break;
			}
		}
	}


	protected void onHandleGeofenceIntent(Context context, Intent intent) {
		GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
		if (geofencingEvent.hasError()) {
			String errorMessage = GeofenceErrorMessages.getErrorString(AppController.instance(),
					geofencingEvent.getErrorCode());
			Log.e(TAG, errorMessage);
			Toast.makeText(context, "onHandleGeofenceIntent Error:" + errorMessage, Toast.LENGTH_LONG).show();
			return;
		}
		int geofenceTransition = geofencingEvent.getGeofenceTransition();
		if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
			 geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
			 geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
			List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
			for (Geofence geofence : triggeringGeofences) {
				GeofenceDto fence = AppController.geofenceRepo().findByID(geofence.getRequestId());
				if (fence != null) {
					executeProfiles(context, geofencingEvent, fence);
				}
			}
		}
		else {
			Log.e(TAG, AppController.instance().getString(R.string.geofence_transition_invalid_type, geofenceTransition));
		}
	}


	private void executeProfiles(Context context, GeofencingEvent geofencingEvent, GeofenceDto geofenceDto) {

		Location triggeringLocation = geofencingEvent.getTriggeringLocation();
		LatLng currentPosition = new LatLng(triggeringLocation.getLatitude(), triggeringLocation.getLongitude());
		List<FhemProfile> profiles = AppController.geofenceActionRepo().getProfilesForGeofence(geofenceDto.getId());

		//TODO: concat all observables and execute them in parallel with only one single startUpdateNotificationIntentService() call
		for (FhemProfile profile : profiles) {
			Class<? extends GeofenceAction> geofenceActionClass = profile.getType().getGeofenceActionClass();
			if (geofenceActionClass == null) {
				Log.d(TAG, "No action class for profile " + profile);
				continue;
			}
			try {
				GeofenceAction<FhemProfile> caller = geofenceActionClass.getConstructor(Context.class).newInstance(context);
				Observable<GeofenceAction.ActionResponse> action = null;
				if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
					action = caller.enter(geofenceDto, profile, currentPosition);
				}
				else if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_EXIT) {
					action = caller.leave(geofenceDto, profile, currentPosition);
				}
				if (action != null) {
					GeofenceProfileState state =
							new GeofenceProfileState(profile.getID(), geofenceDto.getId(), currentPosition,
									geofencingEvent.getGeofenceTransition());
					action.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
							.doOnTerminate(GeofenceBroadcastReceiver::startUpdateNotificationIntentService)
							.subscribe(
									response -> AppController.geofenceStateRepo().add(state.assignResponse(response)),
									error -> {
										AppController.geofenceStateRepo().add(state.assignError(error));
										Toast.makeText(AppController.instance(), error.getMessage(), Toast.LENGTH_LONG).show();
									});
				}
			}
			catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
				Log.e(TAG, e.getMessage(), e);
				Toast.makeText(context, "Can't execute Action:" + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		//TODO: wait until all subscriptions are done and what about re-try the actions multiple time (network failures)
	}

	private static void startUpdateNotificationIntentService() {
		AppController.instance().startService(new Intent(AppController.instance(), UpdateNotificationIntentService.class));
	}
}
