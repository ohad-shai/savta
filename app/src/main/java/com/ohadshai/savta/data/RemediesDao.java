package com.ohadshai.savta.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ohadshai.savta.entities.Remedy;

import java.util.List;

/**
 * Represents the data access object to the remedies table.
 */
@Dao
public interface RemediesDao {

    @Insert
    void create(Remedy remedy);

    @Query("SELECT * FROM Remedies WHERE _id = :id")
    LiveData<Remedy> get(int id);

    @Query("SELECT * FROM Remedies")
    LiveData<List<Remedy>> getAll();

    @Update
    void update(Remedy remedy);

    @Delete
    void delete(Remedy remedy);

}
