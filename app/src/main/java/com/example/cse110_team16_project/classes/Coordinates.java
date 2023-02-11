package com.example.cse110_team16_project.classes;

import android.location.Location;
import android.util.Pair;

import androidx.annotation.NonNull;

public class Coordinates extends Pair<Double,Double>{

    public Coordinates(double latitude, double longitude){
        super(latitude,longitude);
    }

    public Coordinates(Location location){
        super(location.getLatitude(),location.getLongitude());
    }

    public Coordinates(String string){
        super(Double.valueOf(string.split(",",2)[0]),
                Double.valueOf(string.split(",",2)[1]));
    }

    public double getLongitude(){
        return this.second;
    }

    public double getLatitude(){
        return this.first;
    }

    @Override
    @NonNull
    public String toString(){
        return this.first + "," + this.second;
    }
    //https://www.movable-type.co.uk/scripts/latlong.html
    //initial bearing
    public float bearingTo(Coordinates c){
        double lng1 = this.getLongitude();
        double lng2 = c.getLongitude();
        double lat1 = this.getLatitude();
        double lat2 = c.getLatitude();
        double dLon = (lng2-lng1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) - Math.sin(lat1)*Math.cos(lat2)*Math.cos(dLon);
        double theta = (Math.atan2(y, x));
        return (float) ((Math.toDegrees(theta) + 360) % 360);

    }
}
