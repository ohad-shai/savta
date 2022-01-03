package com.ohadshai.savta.data.sql;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ohadshai.savta.data.utils.DateConverter;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.utils.ApplicationContext;

@Database(entities = {Remedy.class}, version = 1)
@TypeConverters({DateConverter.class})
abstract class AppLocalDbRepository extends RoomDatabase {
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
