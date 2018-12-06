package de.a0zero.geofence4fhem.app;

import android.app.Application;
import android.util.Log;

import de.a0zero.geofence4fhem.data.FhemProfileRepo;
import de.a0zero.geofence4fhem.data.GeofenceRepo;

import static android.content.ContentValues.TAG;

public class AppController extends Application {

    private static AppController mInstance;
    private static GeofenceRepo geofenceRepo;
    private static FhemProfileRepo fhemProfileRepo;

    public static AppController instance() {
        return mInstance;
    }

    public static GeofenceRepo geofenceRepo() {
        return geofenceRepo;
    }

    public static FhemProfileRepo fhemProfileRepo() {
        return fhemProfileRepo;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mInstance = this;
        geofenceRepo = new GeofenceRepo(this);
        fhemProfileRepo = new FhemProfileRepo(this);
    }
}
