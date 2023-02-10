package com.example.cse110_team16_project.classes;

import android.location.Location;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertHomes(Home... homes);

    @Update
    public void updateHomes(Home... homes);

    @Delete
    public void deleteHomes(Home... homes);

    @Query("SELECT * FROM Home WHERE label = :id")
    public Home loadHomeById(String id);

    @Query("SELECT * FROM Home")
    public List<Home> loadAllHomes();

    @Query("SELECT coordinates FROM Home WHERE label = :id")
    public Coordinates getCoordinatesByID(String id);

    @Query("SELECT coordinates FROM Home")
    public List<Coordinates> getAllCoordinates();

    @Query("SELECT label FROM Home WHERE label = :id")
    public String getLabelByID(String id);

    @Query("SELECT label FROM Home")
    public List<String> getAllLabels();
}
