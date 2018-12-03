package de.a0zero.geofence4fhem.transition;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * https://stackoverflow.com/users/5280641/neria-nachum
 *
 */
public class LocationServicesManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationServicesManager.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private Context context;

    public GeofencesManager geofencesManager;

    private OnGoogleServicesConnectedListener onGoogleServicesConnectedListener;

    public LocationServicesManager(Context context,
                                   OnGoogleServicesConnectedListener onGoogleServicesConnectedListener) {
        this.context = context;
        this.onGoogleServicesConnectedListener = onGoogleServicesConnectedListener;
        buildGoogleApiClient(context);
    }

    public void GeofencesManager() {
        geofencesManager = new GeofencesManager();
    }

    //region Definition, handling connection
    private synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(Bundle connectionHint) {
        onGoogleServicesConnectedListener.onGoogleServicesConnected();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // Trying to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    //endregion

    public class GeofencesManager {

        private ArrayList<Geofence> mGeofenceList = new ArrayList<>();

        private PendingIntent mGeofencePendingIntent = null;

        private GeofencesManager() {

        }

        public void addGeofenceToList(String key, long expirationDuration, LatLng location, int radius) {
            if (location != null) {
                mGeofenceList.add(new Geofence.Builder()
                        .setRequestId(key)
                        .setCircularRegion(location.latitude, location.longitude, radius)
                        .setExpirationDuration(expirationDuration)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL
                                | Geofence.GEOFENCE_TRANSITION_ENTER
                                | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setLoiteringDelay(1000 * 30)
                        .build());
            }
        }

        /**
         * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
         * Either method can complete successfully or with an error.
         */
        public void onResult(@NonNull Status status) {
            if (status.isSuccess()) {
                Log.i(TAG, "onResult: " + status.toString());
            } else {
                Log.e(TAG, getGeofenceErrorString(status.getStatusCode()));
            }
        }

        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
                Log.i(TAG, "onResult: SUCCESS");
            } else {
                String errorMessage = GeofenceErrorMessages.getErrorString(context, task.getException());
                Log.e(TAG, errorMessage);
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
        private GeofencingRequest getGeofencingRequest() {
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

            // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
            // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
            // is already inside that geofence.
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

            // Add the geofences to be monitored by geofencing service.
            // Empty mGeofenceList leads to crash
            builder.addGeofences(mGeofenceList);

            return builder.build();
        }

        public void addGeofences() {
            if (mGeofenceList.size() > 0) {
                try {
/*
                    LocationServices.GeofencingApi.addGeofences(
                            mGoogleApiClient,
                            getGeofencingRequest(),
                            getGeofencePendingIntent()).setResultCallback(this::onResult);
*/

                    LocationServices.getGeofencingClient(context).addGeofences(
                            getGeofencingRequest(),
                            getGeofencePendingIntent())
                            .addOnCompleteListener(this::onComplete);
                    Log.i(TAG, String.format("Added %d Geofances into System", mGeofenceList.size()));

                } catch (SecurityException securityException) {
                    //Crashlytics.logException(securityException);
                    Log.e(TAG, "Missing permission ACCESS_FINE_LOCATION", securityException);
                }
            }
        }

        public void removeGeofences() {
            if (mGeofenceList.size() > 0) {
                LocationServices.GeofencingApi.removeGeofences(
                        mGoogleApiClient,
                        getGeofencePendingIntent()
                ).setResultCallback(this::onResult); // Result processed in onResult().
            }
        }
    }

    public static String getGeofenceErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "Geofence service is not available now";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Your app has registered too many geofences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "You have provided too many PendingIntents to the addGeofences() call";
            default:
                return "Unknown error: the Geofence service is not available now";
        }
    }
}