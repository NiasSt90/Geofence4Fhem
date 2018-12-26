package de.a0zero.geofence4fhem.transition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.App;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
			String errorMessage = GeofenceErrorMessages.getErrorString(App.instance(),
					geofencingEvent.getErrorCode());
			Log.e(TAG, errorMessage);
			Toast.makeText(context, "onHandleGeofenceIntent Error:" + errorMessage, Toast.LENGTH_LONG).show();
			return;
		}
		GeofenceProfileExecutor geofenceProfileExecutor = new GeofenceProfileExecutor(context);
		int geofenceTransition = geofencingEvent.getGeofenceTransition();
		List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
		LatLng triggeringLocation = new LatLng(geofencingEvent.getTriggeringLocation().getLatitude(),
				geofencingEvent.getTriggeringLocation().getLongitude());
		for (Geofence geofence : triggeringGeofences) {
			GeofenceDto fence = App.geofenceRepo().findByID(geofence.getRequestId());
			if (fence != null) {
				geofenceProfileExecutor.execute(fence, triggeringLocation, geofenceTransition)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe();
			}
		}
	}

}
