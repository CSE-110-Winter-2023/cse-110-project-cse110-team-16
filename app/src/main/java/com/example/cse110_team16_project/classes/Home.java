package com.example.cse110_team16_project.classes;

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
    public Home(Coordinates coordinates, @NonNull String label){
        this.coordinates = coordinates;
        this.label = label;
    }
    public void setCoordinates(Coordinates coordinates){
        this.coordinates = coordinates;
    }
    public Coordinates getCoordinates(){
        return this.coordinates;
    }
    @NonNull
    public String getLabel(){
        return this.label;
    }
}

