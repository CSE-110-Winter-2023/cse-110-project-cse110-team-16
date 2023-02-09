package com.example.cse110_team16_project.classes;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={Home.class},version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract HomeDao homeDao();

    public static final String NAME = "AppDataBase";
}
