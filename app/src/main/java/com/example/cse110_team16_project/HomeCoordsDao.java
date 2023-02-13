package com.example.cse110_team16_project;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HomeCoordsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(HomeCoords coords);

    @Update
    void update(HomeCoords coords);

    @Query("SELECT * from HomeCoords ORDER by label ASC")
    LiveData<List<HomeCoords>> getHomeCoords();

    @Query("DELETE from HomeCoords")
    void deleteAll();
}
