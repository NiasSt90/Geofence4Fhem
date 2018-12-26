package de.a0zero.geofence4fhem;

import android.app.Application;
import android.util.Log;
import de.a0zero.geofence4fhem.data.GeofenceDatabase;
import de.a0zero.geofence4fhem.data.dao.GeofenceDao;
import de.a0zero.geofence4fhem.data.dao.GeofenceProfileStateDao;
import de.a0zero.geofence4fhem.data.dao.GeofenceProfilesDao;
import de.a0zero.geofence4fhem.data.dao.ProfileDAO;

import static android.content.ContentValues.TAG;

public class App extends Application {

    private static App mInstance;

    public static App instance() {
        return mInstance;
    }

    public static GeofenceDao geofenceRepo() {
        return GeofenceDatabase.getInstance(mInstance).geofenceRepo();
    }

    public static ProfileDAO profileDAO() {
        return GeofenceDatabase.getInstance(mInstance).profileDAO();
    }

    public static GeofenceProfilesDao geofenceActionRepo() {
        return GeofenceDatabase.getInstance(mInstance).geofenceProfilesRepo();
    }

    public static GeofenceProfileStateDao geofenceStateRepo() {
    	return GeofenceDatabase.getInstance(mInstance).geofenceProfileStateRepo();
	 }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mInstance = this;
    }
}
