package de.a0zero.geofence4fhem.profiles.ringer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.app.App;
import de.a0zero.geofence4fhem.data.GeofenceDto;
import de.a0zero.geofence4fhem.data.Profile;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;
import io.reactivex.Observable;


public class GeofenceActionChangeRingerSettings implements GeofenceAction<Profile> {

	@Override
	public Observable<ActionResponse> enter(GeofenceDto geofence, Profile profile, LatLng currentPosition) {
		Integer ringerModeEnter = profile.data(RingerSettings.class).getRingerModeEnter();
		return changeRingtone(ringerModeEnter);
	}


	@Override
	public Observable<ActionResponse> leave(GeofenceDto geofence, Profile profile, LatLng currentPosition) {
		Integer ringerModeEnter = profile.data(RingerSettings.class).getRingerModeLeave();
		return changeRingtone(ringerModeEnter);
	}


	private Observable<ActionResponse> changeRingtone(Integer ringerMode) {
		NotificationManager nm =
				(NotificationManager) App.instance().getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted()) {
			App.instance().startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
			return Observable.just(() -> "Missing Permission NOTIFICATION_POLICY_ACCESS_SETTINGS");
		}
		if (ringerMode != null) {
			setRingerMode(App.instance(), ringerMode);
			return Observable.just(() -> "Ringtone switched to " + ringerMode);
		}
		return Observable.just(() -> "Ringtone unchanged");
	}


	/**
	 * mode variable value can be:
	 * AudioManager.RINGER_MODE_SILENT
	 * AudioManager.RINGER_MODE_NORMAL
	 * AudioManager.RINGER_MODE_VIBRATE
	 */
	private void setRingerMode(Context context, int mode) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && nm.isNotificationPolicyAccessGranted()) {
			audioManager.setRingerMode(mode);
		}
	}
}
