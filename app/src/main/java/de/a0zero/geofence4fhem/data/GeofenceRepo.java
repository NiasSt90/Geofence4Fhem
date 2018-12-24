package de.a0zero.geofence4fhem.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface GeofenceRepo {

	@Query("SELECT * FROM GeofenceDto")
	List<GeofenceDto> listAll();

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void add(GeofenceDto geofence);

	@Update(onConflict = OnConflictStrategy.REPLACE)
	int update(GeofenceDto geofenceDto);

	@Update(onConflict = OnConflictStrategy.REPLACE)
	void updateAll(List<GeofenceDto> geofence);

	@Delete
	void delete(GeofenceDto geofenceDto);

	@Query("SELECT * FROM GeofenceDto WHERE id = :requestId")
	GeofenceDto findByID(String requestId);

	@Query("DELETE FROM GeofenceDto")
	void deleteAll();
}
