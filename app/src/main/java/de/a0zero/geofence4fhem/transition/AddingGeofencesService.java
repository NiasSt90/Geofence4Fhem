package de.a0zero.geofence4fhem.transition;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;


/**
 * Install all of our geofences....
 */
public class AddingGeofencesService extends IntentService {

	private static final String TAG = "AddingGeofencesService";


	public AddingGeofencesService() {
		super(TAG);
	}


	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		new GeofencesManager(this).addGeofences();
	}
}
