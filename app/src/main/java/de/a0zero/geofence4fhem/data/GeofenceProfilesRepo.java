package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class GeofenceProfilesRepo {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void add(GeofenceProfiles geofenceProfiles);

    @Delete
    public abstract void del(GeofenceProfiles geofenceProfiles);

    @Query("SELECT * FROM GeofenceProfiles")
    public abstract List<GeofenceProfiles> listAll();

    @Query("SELECT FhemProfile.* FROM FhemProfile " +
            "INNER JOIN GeofenceProfiles ON fhemprofile.id=geofenceprofiles.profileId " +
            "WHERE geofenceprofiles.geofenceId=:geofenceId")
    public abstract List<FhemProfile> getProfilesForGeofence(final String geofenceId);

    @Query("SELECT GeofenceDto.* FROM GeofenceDto " +
            "INNER JOIN GeofenceProfiles ON GeofenceDto.id=geofenceprofiles.geofenceId " +
            "WHERE geofenceprofiles.profileId=:profileId")
    public abstract List<GeofenceDto> getGeofencesForProfile(final int profileId);

    @Query("SELECT * FROM GeofenceDto " +
            "WHERE GeofenceDto.id NOT IN (SELECT geofenceId FROM GeofenceProfiles WHERE geofenceprofiles.profileId=:profileId)")
    public abstract List<GeofenceDto> getAssignableGeofencesForProfile(final int profileId);

}
