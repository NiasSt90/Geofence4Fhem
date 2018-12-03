package de.a0zero.geofence4fhem.transition;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import de.a0zero.geofence4fhem.R;

public class TrackingService extends Service {


    public static final String CHANNEL_LOCATION_TRACKING = "LocationTracking";
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_LOCATION_TRACKING, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.baseline_location_on_white_24)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_location_on_white_48))
                .setColor(Color.GREEN)
                .setContentTitle("Foreground Tracking Service")
                .setContentText(getString(R.string.geofence_transition_notification_text));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_LOCATION_TRACKING);
        }
        builder.setAutoCancel(false);
        builder.setOngoing(true);

        startForeground(1, builder.build());
    }




    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    private final IBinder musicBind = new TrackingServiceBinder();

    public class TrackingServiceBinder extends Binder {
        public TrackingService getService() {
            return TrackingService.this;
        }
    }


}
