package de.a0zero.geofence4fhem.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import de.a0zero.geofence4fhem.data.dao.GeofenceDao;
import de.a0zero.geofence4fhem.data.dao.GeofenceProfileStateDao;
import de.a0zero.geofence4fhem.data.dao.GeofenceProfilesDao;
import de.a0zero.geofence4fhem.data.dao.ProfileDAO;
import de.a0zero.geofence4fhem.data.entities.GeofenceDto;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfileState;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfiles;
import de.a0zero.geofence4fhem.data.entities.Profile;


@Database(
		entities = {Profile.class, GeofenceDto.class, GeofenceProfiles.class,
						GeofenceProfileState.class},
		version = 8)
@TypeConverters(TypeConvertes.class)
public abstract class GeofenceDatabase extends RoomDatabase {

	private static GeofenceDatabase INSTANCE;


	public abstract ProfileDAO profileDAO();

	public abstract GeofenceDao geofenceRepo();

	public abstract GeofenceProfilesDao geofenceProfilesRepo();

	public abstract GeofenceProfileStateDao geofenceProfileStateRepo();


	private static final Object sLock = new Object();


	public static GeofenceDatabase getInstance(Context context) {
		synchronized (sLock) {
			if (INSTANCE == null) {
				INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
						GeofenceDatabase.class, "geofence.db")
						.allowMainThreadQueries()
						.addMigrations(MIGRATION_2_3)
						.addMigrations(MIGRATION_3_4)
						.addMigrations(MIGRATION_4_6)
						.addMigrations(MIGRATION_6_7)
						.addMigrations(MIGRATION_7_8)
						//.fallbackToDestructiveMigration()
						.build();
			}
			return INSTANCE;
		}
	}

	//TODO: replace Dao usage with injections to avoid these assignee her
	public static void setTestDB(GeofenceDatabase db) {
		INSTANCE = db;
	}


	static final Migration MIGRATION_2_3 = new Migration(2, 3) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			database.execSQL(
					"CREATE TABLE IF NOT EXISTS `GeofenceDto` " +
					"(`id` TEXT NOT NULL, `title` TEXT, `name` TEXT, `position` TEXT, `radius` INTEGER NOT NULL, PRIMARY KEY(`id`))");
		}
	};

	static final Migration MIGRATION_3_4 = new Migration(3, 4) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			database.execSQL(
					"CREATE TABLE IF NOT EXISTS `GeofenceProfiles` " +
					"(`profileId` INTEGER NOT NULL, `geofenceId` TEXT NOT NULL, " +
					"PRIMARY KEY(`profileId`, `geofenceId`), " +
					"FOREIGN KEY(`geofenceId`) REFERENCES `GeofenceDto`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , " +
					"FOREIGN KEY(`profileId`) REFERENCES `FhemProfile`(`ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
		}
	};

	static final Migration MIGRATION_4_6 = new Migration(4, 6) {
		@Override
		public void migrate(SupportSQLiteDatabase database) {
			database.execSQL("CREATE TABLE IF NOT EXISTS `GeofenceProfileState` "
								  + "(`time` INTEGER NOT NULL, `profileId` INTEGER NOT NULL, `geofenceId` TEXT NOT NULL, "
								  + "`location` TEXT, `transition` INTEGER NOT NULL, `success` INTEGER NOT NULL, `message` TEXT, "
								  + "PRIMARY KEY(`profileId`, `geofenceId`, `time`), "
								  + "FOREIGN KEY(`geofenceId`) REFERENCES `GeofenceDto`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , "
								  + "FOREIGN KEY(`profileId`) REFERENCES `FhemProfile`(`ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
			database.execSQL(
					"CREATE  INDEX `index_GeofenceProfileState_geofenceId` ON `GeofenceProfileState` (`geofenceId`)");
		}
	};

	static final Migration MIGRATION_6_7 = new Migration(6, 7) {
		@Override
		public void migrate(SupportSQLiteDatabase db) {
			db.beginTransaction();

			// add new Profile table and copy old data into
			db.execSQL(
					"CREATE TABLE IF NOT EXISTS `Profile` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `label` TEXT, `type` TEXT, `data` TEXT)");
			db.execSQL("INSERT INTO `Profile` SELECT ID, label, type, "
						  + " '{ ' || "
						  + " '\"fhemUrl\"    : \"' ||  fhemUrl   || '\", ' || "
						  + " '\"username\"   : \"' || username   || '\", ' || "
						  + " '\"password\"   : \"' || password   || '\", ' || "
						  + " '\"deviceUUID\" : \"' || deviceUUID || '\" }'"
						  + " FROM `FhemProfile`");

			// Update Foreign Key for GeofenceProfiles to Profile
			db.execSQL("CREATE TABLE `tmp_GeofenceProfiles` (`profileId` INTEGER NOT NULL, `geofenceId` TEXT NOT NULL, "
						  + "PRIMARY KEY(`profileId`, `geofenceId`), "
						  + "FOREIGN KEY(`geofenceId`) REFERENCES `GeofenceDto`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , "
						  + "FOREIGN KEY(`profileId`) REFERENCES `Profile`(`ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
			db.execSQL("INSERT INTO `tmp_GeofenceProfiles` SELECT * FROM `GeofenceProfiles`");
			db.execSQL("DROP TABLE `GeofenceProfiles`");
			db.execSQL("ALTER TABLE `tmp_GeofenceProfiles` RENAME TO `GeofenceProfiles`");

			// Update Foreign Key for GeofenceProfileState to Profile
			db.execSQL(
					"CREATE TABLE IF NOT EXISTS `tmp_GeofenceProfileState` (`time` INTEGER NOT NULL, `profileId` INTEGER NOT NULL, `geofenceId` TEXT NOT NULL, `location` TEXT, `transition` INTEGER NOT NULL, `success` INTEGER NOT NULL, `message` TEXT, "
					+ "PRIMARY KEY(`profileId`, `geofenceId`, `time`), "
					+ "FOREIGN KEY(`geofenceId`) REFERENCES `GeofenceDto`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , "
					+ "FOREIGN KEY(`profileId`) REFERENCES `Profile`(`ID`) ON UPDATE NO ACTION ON DELETE CASCADE )");
			db.execSQL("INSERT INTO `tmp_GeofenceProfileState` SELECT * FROM `GeofenceProfileState`");
			db.execSQL("DROP TABLE `GeofenceProfileState`");
			db.execSQL("ALTER TABLE `tmp_GeofenceProfileState` RENAME TO `GeofenceProfileState`");

			//remove old table
			db.execSQL("DROP TABLE IF EXISTS `FhemProfile`");

			db.setTransactionSuccessful();
			db.endTransaction();
		}
	};

	static final Migration MIGRATION_7_8 = new Migration(7, 8) {
		@Override
		public void migrate(SupportSQLiteDatabase db) {
			db.beginTransaction();
			db.execSQL("ALTER TABLE `GeofenceDto` ADD COLUMN `useDwell` INTEGER NOT NULL DEFAULT(1)");
			db.setTransactionSuccessful();
			db.endTransaction();
		}
	};
}