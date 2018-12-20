package de.a0zero.geofence4fhem.transition;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.GeofenceDto;

import java.util.ArrayList;
import java.util.List;


public class GeofencesManager {

	private static final String TAG = GeofencesManager.class.getSimpleName();

	private final Context context;

	private PendingIntent mGeofencePendingIntent = null;


	public GeofencesManager(Context context) {
		this.context = context;
	}


	public boolean addGeofences() throws SecurityException {
		List<Geofence> geofencesList = buildGeofencesList();
		if (geofencesList.size() > 0) {
			LocationServices.getGeofencingClient(context)
					.addGeofences(getGeofencingRequest(geofencesList), getGeofencePendingIntent())
					.addOnCompleteListener(this::onComplete);
			Log.i(TAG, String.format("Added %d Geofances into System", geofencesList.size()));
			return true;
		}
		return false;
	}


	public void removeGeofences() {
		LocationServices.getGeofencingClient(context)
				.removeGeofences(getGeofencePendingIntent())
				.addOnCompleteListener(this::onComplete);
	}


	private List<Geofence> buildGeofencesList() {
		List<GeofenceDto> geofenceDtos = AppController.geofenceRepo().listAll();
		List<Geofence> result = new ArrayList<>();
		for (GeofenceDto geo : geofenceDtos) {
			result.add(new Geofence.Builder()
					.setRequestId(geo.getId())
					.setCircularRegion(geo.getPosition().latitude, geo.getPosition().longitude, geo.getRadius())
					.setExpirationDuration(Geofence.NEVER_EXPIRE)
					.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
											  | Geofence.GEOFENCE_TRANSITION_ENTER
											  | Geofence.GEOFENCE_TRANSITION_EXIT)
					.setLoiteringDelay(1000 * 30)
					.build());
		}
		return result;
	}


	public void onComplete(@NonNull Task<Void> task) {
		if (task.isSuccessful()) {
			Toast.makeText(context, "TaskOnComplete with Success", Toast.LENGTH_SHORT).show();
		}
		else {
			String errorMessage = GeofenceErrorMessages.getErrorString(context, task.getException());
			Log.e(TAG, errorMessage);
			Toast.makeText(context, "TaskOnComplete Error:" + errorMessage, Toast.LENGTH_LONG).show();
		}
	}


	/**
	 * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
	 * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
	 * current list of geofences.
	 *
	 * @return A PendingIntent for the IntentService that handles geofence transitions.
	 */
	private PendingIntent getGeofencePendingIntent() {
		if (mGeofencePendingIntent != null) {
			return mGeofencePendingIntent;
		}

		Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
		intent.setAction(GeofenceBroadcastReceiver.ACTION_GEOFENCE_UPDATE);
		// We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}


	/**
	 * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
	 * Also specifies how the geofence notifications are initially triggered.
	 */
	@NonNull
	private GeofencingRequest getGeofencingRequest(List<Geofence> geofences) {
		GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

		// The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
		// GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
		// is already inside that geofence.
		builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

		// Add the geofences to be monitored by geofencing service.
		// Empty mGeofenceList leads to crash
		builder.addGeofences(geofences);

		return builder.build();
	}

}
