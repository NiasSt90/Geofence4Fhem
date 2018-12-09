package de.a0zero.geofence4fhem.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {FhemProfile.class, GeofenceDto.class, GeofenceProfiles.class}, version = 4)
@TypeConverters(TypeConvertes.class)
public abstract class GeofenceDatabase extends RoomDatabase {

    private static GeofenceDatabase INSTANCE;

    public abstract ProfileDAO profileDAO();

    public abstract GeofenceRepo geofenceRepo();

    public abstract GeofenceProfilesRepo geofenceProfilesRepo();

    private static final Object sLock = new Object();

    public static GeofenceDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        GeofenceDatabase.class, "geofence.db")
                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION_2_3)
                        .addMigrations(MIGRATION_3_4)
                        .fallbackToDestructiveMigration()
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
}
