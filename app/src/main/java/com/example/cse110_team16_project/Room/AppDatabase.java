package com.example.cse110_team16_project.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.cse110_team16_project.classes.Home;

@Database(entities={Home.class},version=1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract HomeDao homeDao();

    public static final String NAME = "AppDataBase";
}
