package de.a0zero.geofence4fhem.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Test;

import static org.junit.Assert.*;


public class ProfileTest {

	@Test
	public void json() {
		JsonElement expectedVal = new JsonParser().parse(
				"{ \"fhemUrl\" : \"http://url.local\", \"username\":\"mylogin\", \"password\":\"mypassword\", "
				+ "\"deviceUUID\":\"myDevice\" }");

		Profile profile = new Profile();
		profile.setFhemUrl("http://url.local");
		profile.setUsername("mylogin");
		profile.setPassword("mypassword");
		profile.setDeviceUUID("myDevice");
		assertEquals(expectedVal, profile.data);
	}
}