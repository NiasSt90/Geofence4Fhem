package de.a0zero.geofence4fhem.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import androidx.room.Room;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class GeofenceDatabaseTest {

	private static final String TEST_DB = "migration-test";

	@Rule
	public MigrationTestHelper helper;

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
		Profile profile = newDB.profileDAO().findFhemProfileById(1);
		assertEquals(profile.label, "myLabel");
		assertEquals(profile.type, ProfileType.FHEM_NOTIFY);
		assertEquals(profile.getFhemUrl(), "https://example.org/fhem/geo");
		assertEquals(profile.getUsername(), "myLogin");
		assertEquals(profile.getPassword(), "myPassword");
		assertEquals(profile.getDeviceUUID(), "device12345");

		//check associations
		assertEquals(1, newDB.geofenceProfilesRepo().getProfilesForGeofence("myID").size());
		assertEquals(1, newDB.geofenceProfileStateRepo().listLast(10).size());
	}

	private GeofenceDatabase getMigratedRoomDatabase() {
		GeofenceDatabase database = Room.databaseBuilder(
				InstrumentationRegistry.getInstrumentation().getTargetContext(),
				GeofenceDatabase.class, TEST_DB)
				.addMigrations(
						GeofenceDatabase.MIGRATION_2_3,
						GeofenceDatabase.MIGRATION_3_4,
						GeofenceDatabase.MIGRATION_4_6,
						GeofenceDatabase.MIGRATION_6_7)
				.build();
		helper.closeWhenFinished(database);
		return database;
	}

}