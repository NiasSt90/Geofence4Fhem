package de.a0zero.geofence4fhem.app;

import android.app.Application;
import android.util.Log;

import de.a0zero.geofence4fhem.data.GeofenceRepo;

import static android.content.ContentValues.TAG;

public class AppController extends Application {

    private static AppController mInstance;
    private static GeofenceRepo geofenceRepo;

    public static AppController instance() {
        return mInstance;
    }

    public static GeofenceRepo geofenceRepo() {
        return geofenceRepo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mInstance = this;
        geofenceRepo = new GeofenceRepo(this);
    }
}
