package de.a0zero.geofence4fhem.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class GeofenceProfilesRepo {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void add(GeofenceProfiles geofenceProfiles);

    @Delete
    public abstract void del(GeofenceProfiles geofenceProfiles);

    @Query("SELECT * FROM GeofenceProfiles")
    public abstract List<GeofenceProfiles> listAll();

    @Query("SELECT Profile.* FROM Profile " +
            "INNER JOIN GeofenceProfiles ON Profile.id=geofenceprofiles.profileId " +
            "WHERE geofenceprofiles.geofenceId=:geofenceId")
    public abstract List<Profile> getProfilesForGeofence(final String geofenceId);

    @Query("SELECT GeofenceDto.* FROM GeofenceDto " +
            "INNER JOIN GeofenceProfiles ON GeofenceDto.id=geofenceprofiles.geofenceId " +
            "WHERE geofenceprofiles.profileId=:profileId")
    public abstract List<GeofenceDto> getGeofencesForProfile(final int profileId);

    @Query("SELECT * FROM GeofenceDto " +
            "WHERE GeofenceDto.id NOT IN (SELECT geofenceId FROM GeofenceProfiles WHERE geofenceprofiles.profileId=:profileId)")
    public abstract List<GeofenceDto> getAssignableGeofencesForProfile(final int profileId);

}
