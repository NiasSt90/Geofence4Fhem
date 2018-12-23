package de.a0zero.geofence4fhem.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ProfileDAO {

    @Transaction
    public List<Profile> listAllByType(ProfileType type) {
        List<Profile> res = listAll();
        return new ArrayList<>(res);
    }

    public Profile findById(int id) {
        return findFhemProfileById(id);
    }

    @Query("SELECT * FROM Profile WHERE id = :id")
    public abstract Profile findFhemProfileById(int id);

    @Query("SELECT * FROM Profile")
    public abstract List<Profile> listAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long add(Profile profile);

    @Update
    public abstract int update(Profile profile);

    @Delete
    public abstract void delete(Profile profile);
}
