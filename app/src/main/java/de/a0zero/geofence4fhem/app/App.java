package de.a0zero.geofence4fhem.app;

import android.app.Application;
import android.util.Log;
import de.a0zero.geofence4fhem.data.GeofenceDatabase;
import de.a0zero.geofence4fhem.data.GeofenceProfileStateRepo;
import de.a0zero.geofence4fhem.data.GeofenceProfilesRepo;
import de.a0zero.geofence4fhem.data.GeofenceRepo;
import de.a0zero.geofence4fhem.data.ProfileDAO;

import static android.content.ContentValues.TAG;

public class App extends Application {

    private static App mInstance;

    public static App instance() {
        return mInstance;
    }

    public static GeofenceRepo geofenceRepo() {
        return GeofenceDatabase.getInstance(mInstance).geofenceRepo();
    }

    public static ProfileDAO profileDAO() {
        return GeofenceDatabase.getInstance(mInstance).profileDAO();
    }

    public static GeofenceProfilesRepo geofenceActionRepo() {
        return GeofenceDatabase.getInstance(mInstance).geofenceProfilesRepo();
    }

    public static GeofenceProfileStateRepo geofenceStateRepo() {
    	return GeofenceDatabase.getInstance(mInstance).geofenceProfileStateRepo();
	 }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mInstance = this;
    }
}