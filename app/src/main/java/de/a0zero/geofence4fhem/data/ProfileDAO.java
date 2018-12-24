package de.a0zero.geofence4fhem.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public abstract class ProfileDAO {

	@Query("SELECT * FROM Profile WHERE type = :type")
	public abstract List<Profile> listAllByType(ProfileType type);

	@Query("SELECT * FROM Profile WHERE id = :id")
	public abstract Profile findById(int id);

	@Query("SELECT * FROM Profile")
	public abstract List<Profile> listAll();

	@Query("SELECT * FROM Profile")
	public abstract LiveData<List<Profile>> liveList();

	@Query("SELECT g.*, 1 as selected FROM GeofenceDto g WHERE "
			 + "g.id IN (SELECT geofenceId FROM GeofenceProfiles WHERE profileId = :profileID) "
			 + " UNION ALL "
			 + "SELECT g.*, 0 as selected FROM GeofenceDto g WHERE "
			 + "g.id NOT IN (SELECT geofenceId FROM GeofenceProfiles WHERE profileId = :profileID) ")
	public abstract LiveData<List<SelectedGeofence>> selectedGeofences(int profileID);

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	public abstract long add(Profile profile);

	@Update
	public abstract int update(Profile profile);

	@Delete
	public abstract void delete(Profile profile);
}
