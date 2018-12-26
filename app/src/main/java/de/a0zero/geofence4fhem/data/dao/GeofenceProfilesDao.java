package de.a0zero.geofence4fhem.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import de.a0zero.geofence4fhem.data.entities.GeofenceProfiles;
import de.a0zero.geofence4fhem.data.entities.Profile;
import io.reactivex.Single;

import java.util.List;

@Dao
public abstract class GeofenceProfilesDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void add(GeofenceProfiles geofenceProfiles);

    @Delete
    public abstract void del(GeofenceProfiles geofenceProfiles);

	@Query("SELECT Profile.* FROM Profile " +
            "INNER JOIN GeofenceProfiles ON Profile.id=geofenceprofiles.profileId " +
            "WHERE geofenceprofiles.geofenceId=:geofenceId")
    public abstract Single<List<Profile>> listProfilesForGeofence(final String geofenceId);

}
