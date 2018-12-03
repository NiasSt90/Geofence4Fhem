package de.a0zero.geofence4fhem.transition;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.android.gms.location.Geofence;

import java.util.List;

import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.GeofenceDto;

/**
 * Install all persisted geofences....
 */
public class AddingGeofencesService extends IntentService implements OnGoogleServicesConnectedListener {

    private static final String TAG = "AddingGeofencesService";

    LocationServicesManager locationServicesManager;

    public AddingGeofencesService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        locationServicesManager = new LocationServicesManager(this, this);
        locationServicesManager.GeofencesManager();

        //Fetch your geofences from somewhere
        List<GeofenceDto> geofenceDtos = AppController.geofenceRepo().listAll();

        for (GeofenceDto geo : geofenceDtos) {
            locationServicesManager.geofencesManager
                    .addGeofenceToList(geo.getId(), Geofence.NEVER_EXPIRE, geo.getPosition(), geo.getRadius());
        }

        locationServicesManager.connect();
    }

    @Override
    public void onGoogleServicesConnected() {
        locationServicesManager.geofencesManager.addGeofences();
    }
}
