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
    public float bearingTo(@NonNull Coordinates c){
        double lat1 = Math.toRadians(this.getLatitude());
        double lat2 = Math.toRadians(c.getLatitude());
        double lng1 = Math.toRadians(this.getLongitude());
        double lng2 = Math.toRadians(c.getLongitude());
        double dLon = (lng2-lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double theta = (Math.atan2(y, x));
        return (float) ((Math.toDegrees(theta) + 360) % 360);

    }
}
