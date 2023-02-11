package com.example.cse110_team16_project;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {HomeCoords.class}, version = 2)
public abstract class RoomDatabaseClass extends RoomDatabase {
    public abstract HomeCoordsDao homeCoordsDao();
}
