package com.ohadshai.savta.data;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ohadshai.savta.entities.User;

/**
 * Represents the data access object to the users table.
 */
@Dao
public interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void register(User user);

    @Query("SELECT * FROM Users WHERE _id = :userId AND _password = :password")
    User login(int userId, String password);

    @Query("SELECT * FROM Users WHERE _id = :id")
    MutableLiveData<User> get(int id);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

}
