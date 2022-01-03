package com.ohadshai.savta.data.utils;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Represents a date converter for storing dates in the ROOM database.
 */
public class DateConverter {

    @TypeConverter
    public long from(Date value) {
        return value.getTime();
    }

    @TypeConverter
    public Date to(long value) {
        return new Date(value);
    }

}
