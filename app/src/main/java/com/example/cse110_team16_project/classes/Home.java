package com.example.cse110_team16_project.classes;

import android.location.Location;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Home{
    @ColumnInfo
    Location location;
    @ColumnInfo
    @PrimaryKey(autoGenerate=true)
    String label;
    public Home(Location location, String label){
        this.location = location;
        this.label = label;
    }
    public void setLocation(Location location){
        this.location = location;
    }
    public Location getLocation(){
        return this.location;
    }
    public String getLabel(){
        return this.label;
    }
}

