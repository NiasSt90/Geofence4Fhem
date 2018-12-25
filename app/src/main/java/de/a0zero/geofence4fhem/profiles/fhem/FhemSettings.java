package de.a0zero.geofence4fhem.profiles.fhem;

import com.google.gson.JsonObject;
import de.a0zero.geofence4fhem.data.ProfileDataMapper;


/**
 * Map the custom data of the {@link de.a0zero.geofence4fhem.data.Profile#data} to the individual properties
 */
public class FhemSettings implements ProfileDataMapper {

	JsonObject data;


	public FhemSettings(JsonObject data) {
		this.data = data;
	}


	public String getFhemUrl() {
		return data.get("fhemUrl") != null ? data.get("fhemUrl").getAsString() : "";
	}


	public void setFhemUrl(String fhemUrl) {
		this.data.addProperty("fhemUrl", fhemUrl);
	}


	public String getUsername() {
		return data.get("username") != null ? data.get("username").getAsString() : "";
	}


	public void setUsername(String username) {
		data.addProperty("username", username);
	}


	public String getPassword() {
		return data.get("password") != null ? data.get("password").getAsString() : "";
	}


	public void setPassword(String password) {
		this.data.addProperty("password", password);
	}


	public String getDeviceUUID() {
		return data.get("deviceUUID") != null ? data.get("deviceUUID").getAsString() : "";
	}


	public void setDeviceUUID(String deviceUUID) {
		this.data.addProperty("deviceUUID", deviceUUID);
	}


}
