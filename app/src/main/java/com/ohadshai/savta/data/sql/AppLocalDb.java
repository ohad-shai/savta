package com.ohadshai.savta.data.sql;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ohadshai.savta.data.RemediesDao;
import com.ohadshai.savta.data.UsersDao;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.entities.User;
import com.ohadshai.savta.utils.ApplicationContext;

@Database(entities = {User.class, Remedy.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UsersDao usersDao();

    public abstract RemediesDao remediesDao();
}

public class AppLocalDb {
    public static AppLocalDbRepository db = Room.databaseBuilder(
            ApplicationContext.getContext(),
            AppLocalDbRepository.class,
            "savta_db.db")
            .fallbackToDestructiveMigration()
            .build();
}
