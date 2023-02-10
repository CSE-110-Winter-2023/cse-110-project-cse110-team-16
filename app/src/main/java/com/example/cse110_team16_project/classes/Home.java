package com.example.cse110_team16_project.classes;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Home{
    @ColumnInfo
    Coordinates coordinates;
    @ColumnInfo
    @PrimaryKey
    @NonNull
    String label;
    public Home(Coordinates coordinates, String label){
        this.coordinates = coordinates;
        this.label = label;
    }
    public void setCoordinates(Location location){
        this.coordinates = coordinates;
    }
    public Coordinates getLocation(){
        return this.coordinates;
    }
    public String getLabel(){
        return this.label;
    }
}

