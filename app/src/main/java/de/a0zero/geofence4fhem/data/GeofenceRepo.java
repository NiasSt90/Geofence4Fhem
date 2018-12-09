package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
}
