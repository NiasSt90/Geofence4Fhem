package de.a0zero.geofence4fhem.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.google.gson.JsonObject;


/**
 * a profile with some settings, needed for executing on enter/leave of an {@link de.a0zero.geofence4fhem.actions.GeofenceAction} implementation.
 *
 * TODO: because no JOIN-TABLE inheritance not possible in sqlite we need to merge together FhemProfile and Profile into a single class.
 * Profile then can be used for other stuff (change RingTone-Settings, toggle-WLAN/..) and therefore the attributes here must
 * be stored into a json structure blob...
 */
@Entity
public class Profile {

	@PrimaryKey(autoGenerate = true)
	int ID;

	String label;

	ProfileType type;

	JsonObject data;

	public Profile() {
		this.type = ProfileType.FHEM_NOTIFY;
		data = new JsonObject();
	}

	public int getID() {
		return ID;
	}


	public void setID(int ID) {
		this.ID = ID;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	@TypeConverters(TypeConvertes.class)
	public void setType(ProfileType type) {
		this.type = type;
	}


	@TypeConverters(TypeConvertes.class)
	public ProfileType getType() {
		return type;
	}

	@TypeConverters(TypeConvertes.class)
	public JsonObject getData() {
		return data;
	}

	@TypeConverters(TypeConvertes.class)
	public void setData(JsonObject data) {
		this.data = data;
	}


	public String getFhemUrl() {
		return data.get("fhemUrl").getAsString();
	}


	public void setFhemUrl(String fhemUrl) {
		this.data.addProperty("fhemUrl", fhemUrl);
	}


	public String getUsername() {
		return data.get("username").getAsString();
	}


	public void setUsername(String username) {
		data.addProperty("username", username);
	}


	public String getPassword() {
		return data.get("password").getAsString();
	}


	public void setPassword(String password) {
		this.data.addProperty("password", password);
	}


	public String getDeviceUUID() {
		return data.get("deviceUUID").getAsString();
	}


	public void setDeviceUUID(String deviceUUID) {
		this.data.addProperty("deviceUUID", deviceUUID);
	}

}
