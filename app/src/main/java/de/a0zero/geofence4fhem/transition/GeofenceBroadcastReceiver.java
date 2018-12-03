/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.a0zero.geofence4fhem.transition;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.app.MainActivity;
import de.a0zero.geofence4fhem.data.GeofenceDto;

/**
 * Receiver for geofence transition and location changes. send notification messages...
 */
public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastRcv";

    public static final String ACTION_GEOFENCE_UPDATE = "GeofenceUpdate";
    public static final String ACTION_LOCATION_UPDATE = "LocationUpdate";


    /**
     * Receives incoming intents.
     *
     * @param context the application context.
     * @param intent  sent by Location Services. This Intent is provided to Location
     *                Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Enqueues a JobIntentService passing the context and intent as parameters
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_GEOFENCE_UPDATE:
                    onHandleGeofenceIntent(intent);
                    break;
                case ACTION_LOCATION_UPDATE:
                    onHandleLocationIntent(intent);
                    break;
            }
        }
    }

    protected void onHandleGeofenceIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(AppController.instance(),
                    geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            Set<GeofenceDto> triggeredFences = new HashSet<>();
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append(getTransitionString(geofenceTransition)).append(" Zone(s):");
            for (Geofence geofence : triggeringGeofences) {
                GeofenceDto fence = AppController.geofenceRepo().findByID(geofence.getRequestId());
                if (fence != null) {
                    triggeredFences.add(fence);
                    msgBuilder.append(fence.getName()).append(" ");
                }
            }
            sendNotification("Geofence", 1, msgBuilder.toString());
            Log.i(TAG, "Geofence Update:" + msgBuilder.toString());
        } else {
            Log.e(TAG, AppController.instance().getString(R.string.geofence_transition_invalid_type, geofenceTransition));
        }
    }

    protected void onHandleLocationIntent(Intent intent) {
        LocationResult result = LocationResult.extractResult(intent);
        if (result != null) {
            List<Location> locations = result.getLocations();

            StringBuilder msgBuilder = new StringBuilder();
            if (locations.isEmpty()) {
                msgBuilder.append("Unknown location");
            }
            else {
                for (Location location : locations) {
                msgBuilder.append("(");
                msgBuilder.append(location.getLatitude());
                msgBuilder.append(", ");
                msgBuilder.append(location.getLongitude());
                msgBuilder.append(")");
                msgBuilder.append("\n");
                }
            }
            sendNotification("Locations", 2, msgBuilder.toString());
            Log.i(TAG, "Location Update:" + msgBuilder.toString());
        }
        else {
            Log.e(TAG, "No LocationResult found in intent....");
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String channel, int id, String notificationDetails) {
        // Get an instance of the Notification manager
        AppController context = AppController.instance();
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(channel, name, NotificationManager.IMPORTANCE_DEFAULT);
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);
        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);
        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // Define the notification settings.
        builder.setSmallIcon(R.drawable.baseline_location_on_white_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.baseline_location_on_white_48))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(context.getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(channel); // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Issue the notification
        mNotificationManager.notify(id, builder.build());
    }


    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return AppController.instance().getString(R.string.geofence_transition_entered);
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return AppController.instance().getString(R.string.geofence_transition_exited);
            default:
                return AppController.instance().getString(R.string.unknown_geofence_transition);
        }
    }
}
