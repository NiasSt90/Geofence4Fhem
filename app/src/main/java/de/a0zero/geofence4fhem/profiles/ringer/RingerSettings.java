package de.a0zero.geofence4fhem.profiles.ringer;

import com.google.gson.JsonObject;
import de.a0zero.geofence4fhem.data.ProfileDataMapper;


public class RingerSettings implements ProfileDataMapper {

	JsonObject data;


	public RingerSettings(JsonObject data) {
		this.data = data;
	}

	public Integer getRingerModeEnter() {
		return data.get("ringerModeEnter") != null ? data.get("ringerModeEnter").getAsInt() : null;
	}

	public Integer getRingerModeLeave() {
		return data.get("ringerModeLeave") != null ? data.get("ringerModeLeave").getAsInt() : null;
	}


	/**
	 * @param mode one of
	 * <pre>
	 * <ul>
	     <li>{@link android.media.AudioManager#RINGER_MODE_SILENT}</li>
	     <li>{@link android.media.AudioManager#RINGER_MODE_VIBRATE}</li>
	     <li>{@link android.media.AudioManager#RINGER_MODE_NORMAL}</li>
	  </ul>
	 </pre>
	 */
	public void setRingerModeEnter(Integer mode) {
		data.addProperty("ringerModeEnter", mode);
	}

	public void setRingerModeLeave(Integer mode) {
		data.addProperty("ringerModeLeave", mode);
	}
}
