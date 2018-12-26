package de.a0zero.geofence4fhem.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.a0zero.geofence4fhem.data.entities.Profile;
import de.a0zero.geofence4fhem.profiles.fhem.FhemSettings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class ProfileTest {

	@Test
	public void json() {
		JsonElement expectedVal = new JsonParser().parse(
				"{ \"fhemUrl\" : \"http://url.local\", \"username\":\"mylogin\", \"password\":\"mypassword\", "
				+ "\"deviceUUID\":\"myDevice\" }");

		Profile profile = new Profile(ProfileType.FHEM_NOTIFY);
		profile.data(FhemSettings.class).setFhemUrl("http://url.local");
		profile.data(FhemSettings.class).setUsername("mylogin");
		profile.data(FhemSettings.class).setPassword("mypassword");
		profile.data(FhemSettings.class).setDeviceUUID("myDevice");
		assertEquals(expectedVal, profile.getData());
	}
}