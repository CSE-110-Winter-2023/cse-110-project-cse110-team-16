package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.util.Pair;

import androidx.annotation.NonNull;

public class Coordinates extends Pair<Double,Double>{

    public Coordinates(double latitude, double longitude){
        super(latitude,longitude);
    }

    public double getLongitude(){
        return this.second;
    }

    public double getLatitude(){
        return this.first;
    }

    //https://www.movable-type.co.uk/scripts/latlong.html
    //initial bearing
    //currently returns 0 if both coordinates are the same
    //TODO: maybe move to Utilities
    public Degrees bearingTo(@NonNull Coordinates c){
        Location a = Converters.CoordinatesToLocation(this);
        Location b = Converters.CoordinatesToLocation(c);
        return new Degrees(a.bearingTo(b));
    }

    public double distanceTo(@NonNull Coordinates c){
        Location a = Converters.CoordinatesToLocation(this);
        Location b = Converters.CoordinatesToLocation(c);
        return a.distanceTo(b);
    }

}
