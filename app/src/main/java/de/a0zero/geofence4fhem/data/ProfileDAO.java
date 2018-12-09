package de.a0zero.geofence4fhem.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ProfileDAO {

    @Transaction
    public List<Profile> listAllByType(ProfileType type) {
        List<FhemProfile> res = listAll();
        return new ArrayList<>(res);
    }

    public Profile findById(int id) {
        return findFhemProfileById(id);
    }

    @Query("SELECT * FROM FhemProfile WHERE id = :id")
    public abstract FhemProfile findFhemProfileById(int id);

    @Query("SELECT * FROM FhemProfile")
    public abstract List<FhemProfile> listAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long add(FhemProfile profile);

    @Update
    public abstract int update(FhemProfile profile);

    @Delete
    public abstract void delete(FhemProfile profile);
}
