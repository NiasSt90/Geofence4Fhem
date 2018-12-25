package de.a0zero.geofence4fhem.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import com.google.android.gms.maps.model.LatLng;
import de.a0zero.geofence4fhem.LiveDataTestUtil;
import de.a0zero.geofence4fhem.profiles.fhem.FhemSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class GeofenceDatabaseTest {

	private static final String TEST_DB = "migration-test1";

	@Rule
	public MigrationTestHelper helper;

	@Rule
	public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


	public GeofenceDatabaseTest() {
		helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
				GeofenceDatabase.class.getName(),
				new FrameworkSQLiteOpenHelperFactory());
	}


	@Before
	public void setUp() throws Exception {
	}


	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void migrate6_to_7() throws IOException {
		SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);
		ContentValues profileData = new ContentValues();
		profileData.put("ID", 1);
		profileData.put("label", "myLabel");
		profileData.put("type", TypeConvertes.fromType(ProfileType.FHEM_NOTIFY));
		profileData.put("fhemUrl", "https://example.org/fhem/geo");
		profileData.put("username", "myLogin");
		profileData.put("password", "myPassword");
		profileData.put("deviceUUID", "device12345");
		db.insert("FhemProfile", SQLiteDatabase.CONFLICT_REPLACE, profileData);

		ContentValues geofence = new ContentValues();
		geofence.put("id", "myID");
		geofence.put("radius", 200);
		db.insert("GeofenceDto", SQLiteDatabase.CONFLICT_REPLACE, geofence);

		ContentValues geo2profile = new ContentValues();
		geo2profile.put("profileId", 1);
		geo2profile.put("geofenceId", "myID");
		db.insert("GeofenceProfiles", SQLiteDatabase.CONFLICT_REPLACE, geo2profile);

		ContentValues geo2profileState = new ContentValues();
		Date date = new Date();
		geo2profileState.put("time", TypeConvertes.toLong(date));
		geo2profileState.put("profileId", 1);
		geo2profileState.put("transition", 1);
		geo2profileState.put("success", true);
		geo2profileState.put("geofenceId", "myID");
		db.insert("GeofenceProfileState", SQLiteDatabase.CONFLICT_REPLACE, geo2profileState);

		db.close();

		GeofenceDatabase newDB = getMigratedRoomDatabase();
		Profile profile = newDB.profileDAO().findById(1);
		assertEquals(profile.label, "myLabel");
		assertEquals(profile.type, ProfileType.FHEM_NOTIFY);
		assertEquals(profile.data(FhemSettings.class).getFhemUrl(), "https://example.org/fhem/geo");
		assertEquals(profile.data(FhemSettings.class).getUsername(), "myLogin");
		assertEquals(profile.data(FhemSettings.class).getPassword(), "myPassword");
		assertEquals(profile.data(FhemSettings.class).getDeviceUUID(), "device12345");

		//check associations
		assertEquals(1, newDB.geofenceProfilesRepo().getProfilesForGeofence("myID").size());
		assertEquals(1, newDB.geofenceProfileStateRepo().listLast(10).size());
	}


	@Test
	public void selectedGeofences() throws IOException, InterruptedException {
		helper.createDatabase(TEST_DB, 7).close();//create fresh db
		GeofenceDatabase newDB = getMigratedRoomDatabase();

		GeofenceDto geofenceDto1 = new GeofenceDto(new LatLng(0,0));
		newDB.geofenceRepo().add(geofenceDto1);
		GeofenceDto geofenceDto2 = new GeofenceDto(new LatLng(1,0));
		newDB.geofenceRepo().add(geofenceDto2);

		long profile1ID = newDB.profileDAO().add(new Profile(ProfileType.FHEM_NOTIFY));
		newDB.geofenceProfilesRepo().add(new GeofenceProfiles((int) profile1ID, geofenceDto1.getId()));

		long profile2ID = newDB.profileDAO().add(new Profile(ProfileType.FHEM_NOTIFY));

		long profile3ID = newDB.profileDAO().add(new Profile(ProfileType.FHEM_NOTIFY));
		newDB.geofenceProfilesRepo().add(new GeofenceProfiles((int) profile3ID, geofenceDto1.getId()));
		newDB.geofenceProfilesRepo().add(new GeofenceProfiles((int) profile3ID, geofenceDto2.getId()));

		List<GeofenceDto> geofenceDtoList = newDB.geofenceRepo().listAll();
		assertEquals(2, geofenceDtoList.size());

		//Profile 1 has one geofence assigned
		List<SelectedGeofence> selectedGeofences = LiveDataTestUtil.getValue(newDB.profileDAO().selectedGeofences((int) profile1ID));
		assertEquals(geofenceDtoList.size(), selectedGeofences.size());
		assertTrue(selectedGeofences.get(0).selected);
		assertEquals(selectedGeofences.get(0).geofence.getId(), geofenceDto1.getId());
		assertFalse(selectedGeofences.get(1).selected);
		assertEquals(selectedGeofences.get(1).geofence.getId(), geofenceDto2.getId());

		//Profile 2 has no geofence assigned
		selectedGeofences = LiveDataTestUtil.getValue(newDB.profileDAO().selectedGeofences((int) profile2ID));
		assertEquals(geofenceDtoList.size(), selectedGeofences.size());
		assertEquals(selectedGeofences.get(0).geofence.getId(), geofenceDto1.getId());
		assertFalse(selectedGeofences.get(0).selected);
		assertEquals(selectedGeofences.get(1).geofence.getId(), geofenceDto2.getId());
		assertFalse(selectedGeofences.get(1).selected);

		//Profile 3 has both geofences assigned
		selectedGeofences = LiveDataTestUtil.getValue(newDB.profileDAO().selectedGeofences((int) profile3ID));
		assertEquals(selectedGeofences.get(0).geofence.getId(), geofenceDto1.getId());
		assertTrue(selectedGeofences.get(0).selected);
		assertEquals(selectedGeofences.get(1).geofence.getId(), geofenceDto2.getId());
		assertTrue(selectedGeofences.get(1).selected);

	}


	private GeofenceDatabase getMigratedRoomDatabase() {
		GeofenceDatabase database = Room.databaseBuilder(
				InstrumentationRegistry.getInstrumentation().getTargetContext(),
				GeofenceDatabase.class, TEST_DB)
				.allowMainThreadQueries()
				.addMigrations(
						GeofenceDatabase.MIGRATION_2_3,
						GeofenceDatabase.MIGRATION_3_4,
						GeofenceDatabase.MIGRATION_4_6,
						GeofenceDatabase.MIGRATION_6_7
				)
				.build();
		helper.closeWhenFinished(database);
		return database;
	}

}