package com.example.cse110_team16_project.Database;
import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cse110_team16_project.classes.SCLocation;

@Database(entities = {SCLocation.class}, version = 1, exportSchema = false)
public abstract class SCLocationDatabase extends RoomDatabase {
    private volatile static SCLocationDatabase instance = null;

    public abstract SCLocationDao getDao();

    public synchronized static SCLocationDatabase provide(Context context) {
        if (instance == null) {
            instance = SCLocationDatabase.make(context);
        }
        return instance;
    }

    private static SCLocationDatabase make(Context context) {
        return Room.databaseBuilder(context, SCLocationDatabase.class, "SC_app.db")
                //.allowMainThreadQueries()
                .build();
    }

    @VisibleForTesting
    public static void inject(SCLocationDatabase testDatabase) {
        if (instance != null ) {
            instance.close();
        }
        instance = testDatabase;
    }

}
