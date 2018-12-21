package de.a0zero.geofence4fhem.transition;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.app.AppController;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.GeofenceProfileState;

import java.util.List;


/**
 * simple intent service to create/update the notification with the current (last 6 events) state of the geofences.
 */
public class UpdateNotificationIntentService extends IntentService {

	private static final String TAG = UpdateNotificationIntentService.class.getSimpleName();


	public UpdateNotificationIntentService() {
		super(UpdateNotificationIntentService.class.getSimpleName());
	}


	@Override
	protected void onHandleIntent(@Nullable Intent intent) {
		List<GeofenceProfileState> stateList = AppController.geofenceStateRepo().listLast(6);
		if (stateList.isEmpty()) {
			return;
		}

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		createNotificationChannel(notificationManager);
		NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
		for (GeofenceProfileState state : stateList) {
			Log.d(TAG, String.format("FID=%s E/L/D=%d msg=%s %tc", state.getGeofenceId(), state.getTransition(), state.getMessage(), state.getTime()));
			style.addLine(createNotificationMsgFromState(state));
		}
		GeofenceProfileState lastState = stateList.get(0);
		Spanned contentText = createNotificationMsgFromState(lastState);
		NotificationCompat.Builder builder =
				new NotificationCompat.Builder(this, TrackingService.CHANNEL_LOCATION_TRACKING)
						.setSmallIcon(R.drawable.baseline_location_on_white_24)
						.setContentTitle("Last geofence events...")
						.setContentText(contentText)
						.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.baseline_location_on_white_48))
						.setStyle(style)
						.setColor(lastState.isSuccess() ? Color.GREEN : Color.RED)
						.setAutoCancel(false)
						.setOngoing(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			builder.setChannelId(TrackingService.CHANNEL_LOCATION_TRACKING);
		}
		notificationManager.notify(TrackingService.LOCATION_TRACKING_NOTIFY_ID, builder.build());
		AppController.geofenceStateRepo().deleteOldEntries();
	}


	private Spanned createNotificationMsgFromState(GeofenceProfileState state) {
		//$TIME: ${enter/leave/dwell} $ZONE at $POSITION exec=$PROFILE_TYPE result=$SUCCESS_OR_ERROR
		GeofenceDto geofence = AppController.geofenceRepo().findByID(state.getGeofenceId());
		String transition = GeofenceErrorMessages.getTransitionString(state.getTransition());
		String dateTime = DateUtils.formatDateTime(this, state.getTime().getTime(),
				DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_SHOW_WEEKDAY);
		return Html.fromHtml(String.format("%s, <tt>%s</tt> <b>%s</b> res=<i>%s</i>",
				dateTime, transition, geofence.getTitle(), state.getMessage()));
	}


	private void createNotificationChannel(NotificationManager notificationManager) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.app_name);
			NotificationChannel channel =
					new NotificationChannel(TrackingService.CHANNEL_LOCATION_TRACKING, name,
							NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}
	}
}
