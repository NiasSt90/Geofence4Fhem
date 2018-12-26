package de.a0zero.geofence4fhem.profiles.ringer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.App;
import de.a0zero.geofence4fhem.R;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import de.a0zero.geofence4fhem.data.entities.Profile;
import de.a0zero.geofence4fhem.profiles.GeofenceAction;
import io.reactivex.Flowable;


public class GeofenceActionChangeRingerSettings implements GeofenceAction {

	@Override
	public Flowable<ActionResponse> enter(GeofenceDto geofence, Profile profile, LatLng currentPosition) {
		Integer ringerModeEnter = profile.data(RingerSettings.class).getRingerModeEnter();
		return Flowable.fromCallable(() -> changeRingtone(ringerModeEnter));
	}


	@Override
	public Flowable<ActionResponse> leave(GeofenceDto geofence, Profile profile, LatLng currentPosition) {
		Integer ringerModeEnter = profile.data(RingerSettings.class).getRingerModeLeave();
		return Flowable.fromCallable(() -> changeRingtone(ringerModeEnter));
	}


	private ActionResponse changeRingtone(Integer ringerMode) {
		NotificationManager nm =
				(NotificationManager) App.instance().getSystemService(Context.NOTIFICATION_SERVICE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !nm.isNotificationPolicyAccessGranted()) {
			App.instance().startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
			return () -> "Missing Permission NOTIFICATION_POLICY_ACCESS_SETTINGS";
		}
		if (ringerMode != null) {
			setRingerMode(App.instance(), ringerMode);
		}
		return () -> getRingerModeString(ringerMode);
	}

	private String getRingerModeString(Integer ringermode) {
		if (ringermode == null) return App.instance().getString(R.string.ringer_mode_switch_unchanged);
		switch (ringermode) {
			case AudioManager.RINGER_MODE_SILENT:return App.instance().getString(R.string.ringer_mode_switch_silent);
			case AudioManager.RINGER_MODE_VIBRATE:return App.instance().getString(R.string.ringer_mode_switch_vibrate);
			case AudioManager.RINGER_MODE_NORMAL:return App.instance().getString(R.string.ringer_mode_switch_normal);
			default:return App.instance().getString(R.string.ringer_mode_switch_unknown);
		}
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
