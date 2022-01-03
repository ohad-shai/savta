package com.ohadshai.savta.data.sql;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ohadshai.savta.entities.Remedy;

import java.util.List;

/**
 * Represents the data access object to the remedies table.
 */
@Dao
public interface RemediesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Remedy remedy);

    @Query("SELECT * FROM Remedies WHERE _id = :id")
    LiveData<Remedy> get(String id);

    @Query("SELECT * FROM Remedies")
    LiveData<List<Remedy>> getAll();

    @Delete
    void delete(Remedy remedy);

}
