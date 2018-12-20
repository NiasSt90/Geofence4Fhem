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


/**
 * Stupid ongoing foreground service...<a href="https://developer.android.com/about/versions/oreo/background-location-limits">only to fulfill oreo changes</a>
 * and get enter/leave/dwell intents for my registered geofences.
 *
 * TODO: i'm still investigating if it is really needed or if the {@link GeofenceBroadcastReceiver} will receive the
 * intents without this foreground service...
 */
public class TrackingService extends Service {

	public static final String CHANNEL_LOCATION_TRACKING = "LocationTracking";

	public static final int LOCATION_TRACKING_NOTIFY_ID = 1;

	private final IBinder binder = new TrackingServiceBinder();

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
				.setContentTitle("Tracking Service initializing...");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(CHANNEL_LOCATION_TRACKING);
		}
		builder.setAutoCancel(false);
		builder.setOngoing(true);
		startForeground(LOCATION_TRACKING_NOTIFY_ID, builder.build());
		startService(new Intent(this, UpdateNotificationIntentService.class));
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}


	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}


	public class TrackingServiceBinder extends Binder {

		public TrackingService getService() {
			return TrackingService.this;
		}
	}

}
