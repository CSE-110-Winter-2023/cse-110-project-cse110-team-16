package com.example.cse110_team16_project.classes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.cse110_team16_project.classes.Coordinates;

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
    public void setCoordinates(Coordinates coordinates){
        this.coordinates = coordinates;
    }
    public Coordinates getCoordinates(){
        return this.coordinates;
    }
    public String getLabel(){
        return this.label;
    }
}

