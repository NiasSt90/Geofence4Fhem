package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface GeofenceProfileStateRepo {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void add(GeofenceProfileState state);

	@Query("SELECT * FROM GeofenceProfileState ORDER BY time DESC LIMIT :limit")
	List<GeofenceProfileState> listLast(int limit);

	/**
	 * delete all state entries older then 7 days
	 */
	@Query("DELETE FROM GeofenceProfileState WHERE (julianday('now') - time) > 7")
	void deleteOldEntries();

}
