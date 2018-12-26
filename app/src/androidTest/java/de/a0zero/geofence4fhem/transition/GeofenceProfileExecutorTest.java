package de.a0zero.geofence4fhem.transition;

import android.media.AudioManager;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.data.GeofenceDatabase;
import de.a0zero.geofence4fhem.data.ProfileType;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfileState;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfiles;
import de.a0zero.geofence4fhem.data.entities.Profile;
import de.a0zero.geofence4fhem.profiles.fhem.FhemSettings;
import de.a0zero.geofence4fhem.profiles.ringer.RingerSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class GeofenceProfileExecutorTest {

	private GeofenceDatabase db;

	@Rule
	public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


	@Before
	public void setUp() throws Exception {
		db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
				GeofenceDatabase.class)
				.allowMainThreadQueries()
				.build();
		GeofenceDatabase.setTestDB(db);
	}


	@After
	public void tearDown() throws Exception {
		db.close();
	}


	@Test
	public void execute() throws InterruptedException {
		Profile p1 = new Profile(ProfileType.FHEM_NOTIFY);
		FhemSettings fhemSettings = p1.data(FhemSettings.class);
		fhemSettings.setFhemUrl("https://nias.zapto.org/fhem/geo");
		fhemSettings.setUsername("user");
		fhemSettings.setPassword("password");
		int profile1ID = (int) db.profileDAO().add(p1);

		Profile p2 = new Profile(ProfileType.RINGER_SETTINGS);
		RingerSettings ringerSettings = p2.data(RingerSettings.class);
		ringerSettings.setRingerModeEnter(AudioManager.RINGER_MODE_SILENT);
		ringerSettings.setRingerModeLeave(AudioManager.RINGER_MODE_VIBRATE);
		int profile2ID = (int) db.profileDAO().add(p2);

		LatLng position = new LatLng(0, 0);
		GeofenceDto geofence = new GeofenceDto(position);
		geofence.setName("home");
		geofence.setRadius(100);
		db.geofenceRepo().add(geofence);

		db.geofenceProfilesRepo().add(new GeofenceProfiles(profile1ID, geofence.getId()));
		db.geofenceProfilesRepo().add(new GeofenceProfiles(profile2ID, geofence.getId()));

		GeofenceProfileExecutor executor =
				new GeofenceProfileExecutor(InstrumentationRegistry.getInstrumentation().getTargetContext());

		List<GeofenceProfileState> result = executor.execute(geofence, position, Geofence.GEOFENCE_TRANSITION_ENTER)
				.test().await().values();

		assertEquals(2, result.size());
		assertEquals(profile1ID, result.get(0).getProfileId());
		assertFalse(result.get(0).isSuccess());
		assertEquals(profile2ID, result.get(1).getProfileId());
		assertTrue(result.get(1).isSuccess());
		assertEquals(2, db.geofenceProfileStateRepo().listLast(4).size());

		result = executor.execute(geofence, position, -1)
				.test().await().values();

		assertEquals(2, result.size());
		assertFalse(result.get(0).isSuccess());
		assertEquals("Unknown transition type", result.get(0).getMessage());
		assertFalse(result.get(1).isSuccess());
		assertEquals("Unknown transition type", result.get(1).getMessage());
		assertEquals(4, db.geofenceProfileStateRepo().listLast(4).size());
	}
}