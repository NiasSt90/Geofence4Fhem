package de.a0zero.geofence4fhem.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {FhemProfile.class, GeofenceDto.class, GeofenceProfiles.class, GeofenceProfileState.class}, version = 6)
@TypeConverters(TypeConvertes.class)
public abstract class GeofenceDatabase extends RoomDatabase {

    private static GeofenceDatabase INSTANCE;

    public abstract ProfileDAO profileDAO();

    public abstract GeofenceRepo geofenceRepo();

    public abstract GeofenceProfilesRepo geofenceProfilesRepo();

    public abstract GeofenceProfileStateRepo geofenceProfileStateRepo();

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
                        //.fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
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
			database.execSQL("CREATE  INDEX `index_GeofenceProfileState_geofenceId` ON `GeofenceProfileState` (`geofenceId`)");
			database.execSQL("CREATE  INDEX `index_GeofenceProfiles_geofenceId` ON `GeofenceProfiles` (`geofenceId`)");
		}
	};

}
